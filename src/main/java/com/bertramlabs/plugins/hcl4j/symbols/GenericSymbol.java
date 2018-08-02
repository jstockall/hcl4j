/*
 * Copyright 2014 the original author or authors.
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
package com.bertramlabs.plugins.hcl4j.symbols;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class GenericSymbol implements Symbol {
	private Integer line;
	private Integer column;
	private Integer position;
	private Integer length;

	private String name;
	private HCLValue value;

	private Set<Symbol> children = new LinkedHashSet<Symbol>();
	private Set<Symbol> attributes = new LinkedHashSet<Symbol>();
	private Symbol parent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(HCLValue value) {
		this.value = value;
	}

	public HCLValue getValue() {
		return value;
	}


	public Integer getLine() {
		return line;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getColumn() {
		return column;
	}

	public Integer getPosition() {
		return position;
	}

	public Collection<Symbol> getChildren() {
		return children;
	}

	public Collection<Symbol> getAttributes() {
		return attributes;
	}

	public Symbol getParent() {
		return parent;
	}

	public void setParent(Symbol symbol) {
		this.parent = symbol;
	}

	public void appendChild(Symbol symbol) {
		children.add(symbol);
	}

	public void appendAttribute(Symbol symbol) {
		attributes.add(symbol);
	}

	public GenericSymbol(String name) {
		this.name = name;
	}

	public GenericSymbol(String name,HCLValue value,Integer line, Integer column,Integer position) {
		this.name = name;
		this.value = value;
		this.line = line;
		this.column = column;
		this.position = position;
	}

    @Override
    public String toString() {
        return String.format(
                "%s [line=%s, column=%s, position=%s, length=%s, name=%s, value=%s, children=%s, attributes=%s]",
                getSymbolName(), line, column, position, length, name, value, children, attributes);
    }
}
