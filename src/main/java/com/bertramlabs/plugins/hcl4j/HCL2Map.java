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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bertramlabs.plugins.hcl4j.symbols.HCLAttribute;
import com.bertramlabs.plugins.hcl4j.symbols.HCLBlock;
import com.bertramlabs.plugins.hcl4j.symbols.HCLValue;
import com.bertramlabs.plugins.hcl4j.symbols.Symbol;

/**
 * Utility class to create a Map of the HCL configuration
 * 
 * @author jstockall
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HCL2Map extends HCLExport {

    public HCL2Map() {
        super(false);
    }
    
    /**
     * Converts the parsed HCL configuration tree in to a map. Separated from the original HCL parser
     * 
     * @param configuation the result or a HCLParser.parse() invocation
     * @return Mapped result of object tree coming from HCL (values of keys can be variable).
     * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
     * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
     */
    public Map<String,Object> toMap(HCLObject configuation) throws HCLParserException, IOException {
        return toMap(configuation.getRootBlocks());
    }
    
    /**
     * Converts the parsed HCL configuration tree in to a map. Separated from the original HCL parser
     * 
     * @param rootBlocks the root blocks of a HCL configuration
     * @return Mapped result of object tree coming from HCL (values of keys can be variable).
     * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
     * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
     */
    public Map<String,Object> toMap(Collection<Symbol> rootBlocks) throws HCLParserException, IOException {
        //Time to parse the AST Tree into a Map
        Map<String,Object> result = new LinkedHashMap<>();

        Map<String,Object> mapPosition = result;

        for(Symbol currentElement : rootBlocks) {
            if(currentElement instanceof HCLBlock) {
                HCLBlock currentBlock = (HCLBlock) currentElement;
                processBlock(currentBlock,mapPosition);
            } else if(currentElement instanceof HCLAttribute) {
                HCLAttribute attr = (HCLAttribute)currentElement;
                mapPosition.put(attr.getName(),processValue(attr.getValue()));
            }
        }
        return result;
    }

    private void processBlock(HCLBlock block, Map<String,Object> mapPosition) throws HCLParserException {
        for(int counter = 0 ; counter < block.blockNames.size() ; counter++) {
            String blockName = block.blockNames.get(counter);
            if(mapPosition.containsKey(blockName)) {
                if(counter == block.blockNames.size() - 1 && mapPosition.get(blockName) instanceof Map) {
                    List<Map<String,Object>> objectList = new ArrayList<>();
                    Map<String,Object> addedObject = new LinkedHashMap<String,Object>();
                    objectList.add((Map)mapPosition.get(blockName));
                    objectList.add(addedObject);
                    mapPosition.put(blockName,objectList);
                    mapPosition = addedObject;
                } else if(mapPosition.get(blockName) instanceof Map) {
                    mapPosition = (Map<String,Object>) mapPosition.get(blockName);
                } else if(counter == block.blockNames.size() - 1 && mapPosition.get(blockName) instanceof List) {
                    Map<String,Object> addedObject = new LinkedHashMap<String,Object>();
                    ((List<Map>)mapPosition.get(blockName)).add(addedObject);
                    mapPosition = addedObject;
                } else {
                    if(mapPosition.get(blockName) instanceof List) {
                        throw new HCLParserException("HCL Block expression scope traverses an object array");
                    } else {
                        throw new HCLParserException("HCL Block expression scope traverses an object value");
                    }
                }
            } else {
                mapPosition.put(blockName,new LinkedHashMap<String,Object>());
                mapPosition = (Map<String,Object>) mapPosition.get(blockName);
            }
        }

        if(block.getChildren() != null) {
            for(Symbol child : block.getChildren()) {
                if(child instanceof HCLAttribute) {
                    HCLAttribute attr = (HCLAttribute)child;
                    HCLValue value = attr.getValue();
                    mapPosition.put(attr.getName(), processValue(value));
                } else if (child instanceof HCLBlock) {
                    processBlock((HCLBlock)child,mapPosition);
                }
            }
        }
    }
}
