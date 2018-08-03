/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bertramlabs.plugins.hcl4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import com.bertramlabs.plugins.hcl4j.symbols.Symbol;

/**
 * Parser for the Hashicorp Configuration Language (HCL). This is the primary
 * endpoint and converts the HCL syntax into a {@link Map}. This parser utilizes
 * a lexer to generate symbols based on the HCL spec. String interpolation is
 * not evaluated at this point in the parsing.
 *
 * <p>
 * Below is an example of how HCL might be parsed.
 * </p>
 * 
 * <pre>
 *     {@code
 *     import com.bertramlabs.plugins.hcl4j.HCLParser;
 *
 *     File terraformFile = new File("terraform.tf");
 *
 *     HCLObject results = new HCLParser().parse(terraformFile);
 *     }
 * </pre>
 * 
 * @author David Estes (initial parser from https://github.com/bertramdev/hcl4j)
 * @author jstockall Refactored to expose raw symbols and to export back to HCL
 *         text format
 */
public class HCLParser {

    public HCLParser() {

    }

    /**
     * Parses terraform configuration language from a String
     * 
     * @param input
     *            String input containing HCL syntax
     * @return Collection of top level elements in the configuration
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     * @throws IOException
     *             In the event the reader is unable to pull from the input
     *             source this exception is thrown.
     */
    public HCLObject parse(String input) throws HCLParserException, IOException {
        StringReader reader = new StringReader(input);
        return parse(reader);
    }

    /**
     * Parses terraform syntax as it comes from a File.
     * 
     * @param input
     *            A source file to process with a default charset of UTF-8
     * @return Collection of top level elements in the configuration
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     * @throws IOException
     *             In the event the reader is unable to pull from the input
     *             source this exception is thrown.
     */
    public HCLObject parse(File input) throws HCLParserException, IOException {
        return parse(input, "UTF-8");
    }

    /**
     * Parses terraform syntax as it comes from a File. closed at the end of the
     * parse operation (commonly via wrapping in a finally block)
     * 
     * @param input
     *            A source file to process
     * @param cs
     *            A charset
     * @return Collection of top level elements in the configuration
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     * @throws IOException
     *             In the event the reader is unable to pull from the input
     *             source this exception is thrown.
     */
    public HCLObject parse(File input, Charset cs) throws HCLParserException, IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(input);
            return parse(is, cs);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Parses terraform syntax as it comes from a File.
     * 
     * @param input
     *            A source file to process
     * @param charsetName
     *            The name of a supported charset
     * @return Collection of top level elements in the configuration
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     * @throws IOException
     *             In the event the reader is unable to pull from the input
     *             source this exception is thrown.
     * @throws UnsupportedEncodingException
     *             If the charset ( UTF-8 by default if unspecified) encoding is
     *             not supported
     */
    public HCLObject parse(File input, String charsetName) throws HCLParserException, IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(input);
            return parse(is, charsetName);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Parses terraform syntax as it comes from an input stream. The end user is
     * responsible for ensuring the stream is closed at the end of the parse
     * operation (commonly via wrapping in a finally block)
     * 
     * @param input
     *            Streamable input of text going to the lexer
     * @return Collection of top level elements in the configuration
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     * @throws IOException
     *             In the event the reader is unable to pull from the input
     *             source this exception is thrown.
     */
    public HCLObject parse(InputStream input) throws HCLParserException, IOException {
        return parse(input, "UTF-8");
    }

    /**
     * Parses terraform syntax as it comes from an input stream. The end user is
     * responsible for ensuring the stream is closed at the end of the parse
     * operation (commonly via wrapping in a finally block)
     * 
     * @param input
     *            Streamable input of text going to the lexer
     * @param cs
     *            CharSet with which to read the contents of the stream (default
     *            UTF-8)
     * @return Collection of top level elements in the configuration
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     * @throws IOException
     *             In the event the reader is unable to pull from the input
     *             source this exception is thrown.
     */
    public HCLObject parse(InputStream input, Charset cs) throws HCLParserException, IOException {

        InputStreamReader reader;
        if (cs != null) {
            reader = new InputStreamReader(input, cs);
        } else {
            reader = new InputStreamReader(input, "UTF-8");
        }
        return parse(reader);
    }

    /**
     * Parses terraform syntax as it comes from an input stream. The end user is
     * responsible for ensuring the stream is closed at the end of the parse
     * operation (commonly via wrapping in a finally block)
     * 
     * @param input
     *            Streamable input of text going to the lexer
     * @param charsetName
     *            String lookup of the character set this stream is providing
     *            (default UTF-8)
     * @return Collection of top level elements in the configuration
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     * @throws IOException
     *             In the event the reader is unable to pull from the input
     *             source this exception is thrown.
     * @throws UnsupportedEncodingException
     *             If the charset ( UTF-8 by default if unspecified) encoding is
     *             not supported.
     */
    public HCLObject parse(InputStream input, String charsetName)
            throws HCLParserException, IOException, UnsupportedEncodingException {

        InputStreamReader reader;
        if (charsetName != null) {
            reader = new InputStreamReader(input, charsetName);
        } else {
            reader = new InputStreamReader(input, "UTF-8");
        }
        return parse(reader);
    }

    /**
     * Parses terraform configuration language from a Reader
     * 
     * @param reader
     *            A reader object used for absorbing various streams or String
     *            variables containing the hcl code
     * 
     * @return Collection of top level elements in the configuration
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     * @throws IOException
     *             In the event the reader is unable to pull from the input
     *             source this exception is thrown.
     */
    public HCLObject parse(Reader reader) throws HCLParserException, IOException {
        HCLLexer lexer = new HCLLexer(reader);
        ArrayList<Symbol> rootBlocks = new ArrayList<Symbol>();
        Symbol element;
        while ((element = lexer.yylex()) != null) {
            rootBlocks.add(element);
        }

        return new HCLObject(rootBlocks);
    }
}
