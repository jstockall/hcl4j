package com.bertramlabs.plugins.hcl4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bertramlabs.plugins.hcl4j.symbols.HCLValue;

public abstract class HCLExport {

    private final boolean quoteStrings;
    
    protected HCLExport(boolean quoteStrings) {
        this.quoteStrings = quoteStrings;
    }

    protected List<Object> processArray(List<HCLValue> values) throws HCLParserException {
        List<Object> results = new ArrayList<Object>();
        for (HCLValue value : values) {
            results.add(processValue(value));
        }
        return results;
    }

    protected Map<String, Object> processMap(Map<String, HCLValue> values) throws HCLParserException {
        Map<String, Object> results = new LinkedHashMap<>();

        for (String key : values.keySet()) {
            results.put(key, processValue(values.get(key)));
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected Object processValue(HCLValue value) throws HCLParserException {
        if (value.type.equals("string")) {
            return quoteStrings ? String.format("\"%s\"", value.value) : value.value;
        } else if (value.type.equals("boolean")) {
            if (value.value.equals("true")) {
                return new Boolean(true);
            } else {
                return new Boolean(false);
            }
        } else if (value.type.equals("number")) {
            try {
                Double numericalValue = Double.parseDouble((String) (value.value));
                return numericalValue;
            } catch (NumberFormatException ex) {
                throw new HCLParserException("Error Parsing Numerical Value in HCL Attribute ", ex);
            }
        } else if (value.type.equals("array")) {
            return processArray((List<HCLValue>) value.value);
        } else if (value.type.equals("map")) {
            return processMap((Map<String, HCLValue>) value.value);
        } else {
            throw new HCLParserException("HCL Attribute value not recognized by parser (not implemented yet).");
        }
    }
}
