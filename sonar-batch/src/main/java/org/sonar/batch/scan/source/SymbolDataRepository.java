/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.batch.scan.source;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.sonar.api.scan.source.Symbol;

import java.util.Collection;
import java.util.Comparator;

public class SymbolDataRepository {

  private static final String FIELD_SEPARATOR = ",";
  private static final String SYMBOL_SEPARATOR = ";";

  private Multimap<Symbol, Integer> referencesBySymbol;

  public SymbolDataRepository() {
    referencesBySymbol = TreeMultimap.create(new SymbolComparator(), new ReferenceComparator());
  }

  public void registerSymbol(Symbol symbol) {
    referencesBySymbol.put(symbol, symbol.getDeclarationStartOffset());
  }

  public void registerSymbolReference(Symbol symbol, int startOffset) {
    if(startOffset >= symbol.getDeclarationStartOffset() && startOffset < symbol.getDeclarationEndOffset()) {
      throw new UnsupportedOperationException("Cannot add reference overlapping the symbol declaration");
    }
    referencesBySymbol.put(symbol, startOffset);
  }

  public String serializeAsString() {

    StringBuilder serializedData = new StringBuilder();

    for (Symbol symbol : referencesBySymbol.keySet()) {

      serializedData.append(symbol.getDeclarationStartOffset())
              .append(FIELD_SEPARATOR)
              .append(symbol.getDeclarationEndOffset());
      Collection<Integer> symbolReferences = referencesBySymbol.get(symbol);
      for (Integer symbolReference : symbolReferences) {
        serializedData.append(FIELD_SEPARATOR).append(symbolReference);
      }
      serializedData.append(SYMBOL_SEPARATOR);
    }

    return serializedData.toString();
  }


  private class SymbolComparator implements Comparator<Symbol> {
    @Override
    public int compare(Symbol left, Symbol right) {
      return left.getDeclarationStartOffset() - right.getDeclarationStartOffset();
    }
  }

  private class ReferenceComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer left, Integer right) {
      int result;
      if(left != null & right != null) {
        result = left - right;
      } else {
        result = left == null ? -1 : 1;
      }
      return result;
    }
  }
}
