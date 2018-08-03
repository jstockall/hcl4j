/*
 * Copyright 2018 the original author or authors.
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

import com.bertramlabs.plugins.hcl4j.symbols.HCLAttribute;
import com.bertramlabs.plugins.hcl4j.symbols.HCLBlock;
import com.bertramlabs.plugins.hcl4j.symbols.Symbol;

/**
 * Utility class to create a string from the HCL configuration
 * 
 * @author jstockall
 */
public class HCL2String extends HCLExport {

    public HCL2String() {
        super(true);
    }

    /**
     * Converts the symbols to a HCL text document
     * 
     * @param configuration HCL block tree 
     * 
     * @return A HCL configuration as a string
     * 
     * @throws HCLParserException
     *             Any type of parsing errors are returned as this exception if
     *             the syntax is invalid.
     */
    public String toHcl(HCLObject configuration) throws HCLParserException {
        StringBuilder sb = new StringBuilder();
        for (Symbol hcl : configuration.getRootBlocks()) {
            if (hcl instanceof HCLBlock) {
                printBlock((HCLBlock) hcl, sb, 0);
            } else if (hcl instanceof HCLAttribute) {
                HCLAttribute attr = (HCLAttribute) hcl;
                sb.append('\t').append(attr.getName()).append(" = ").append(processValue(attr.getValue())).append('\n');
            }
        }
        return sb.toString();
    }

    private void printBlock(HCLBlock block, StringBuilder sb, int indent) throws HCLParserException {
        for (int counter = 0, size = block.blockNames.size(); counter < size; counter++) {
            String blockName = block.blockNames.get(counter);
            if (counter == 0) {
                for (int i = 0; i < indent; i++) {
                    sb.append('\t');
                }
                sb.append(blockName).append(" ");
                if (size == 1) {
                    sb.append(" {\n");
                }
                indent++;
            } else if (counter == size - 1) {
                sb.append("\"").append(blockName).append("\" {\n");
            } else {
                sb.append("\"").append(blockName).append("\" ");
            }
        }

        for (Symbol child : block.getChildren()) {
            if (child instanceof HCLAttribute) {
                HCLAttribute attr = (HCLAttribute) child;
                for (int i = 0; i < indent; i++) {
                    sb.append('\t');
                }
                sb.append(attr.getName()).append(" = ").append(processValue(attr.getValue())).append('\n');
            } else if (child instanceof HCLBlock) {
                printBlock((HCLBlock) child, sb, indent);
            }
        }
        indent--;
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
        sb.append("}\n\n");
    }

}
