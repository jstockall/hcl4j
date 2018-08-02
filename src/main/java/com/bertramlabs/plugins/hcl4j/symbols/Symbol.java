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

public interface Symbol {
	public String getSymbolName();

	Integer getLine();
	Integer getColumn();
	Integer getPosition();
	Integer getLength();
	void setLength(Integer length);

	String getName();
	void setName(String name);

	HCLValue getValue();
	void setValue(HCLValue value);

	Collection<Symbol> getAttributes();
	void appendAttribute(Symbol symbol);

	Collection<Symbol> getChildren();
	void appendChild(Symbol symbol);

	Symbol getParent();
	void setParent(Symbol parent);
}