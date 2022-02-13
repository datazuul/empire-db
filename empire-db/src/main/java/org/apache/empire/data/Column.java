/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.empire.data;

/**
 * The column interface provides methods for accessing metadata that is only relevant for updating records.
 * <P>
 * This interface inherits from ColumnExpr which provides further metadata.
 */
public interface Column extends ColumnExpr
{
    /**
     *  Predefined column expression attributes (optional)
     *  for method
     *      Object getAttribute(String name)  
     */
    public static final String COLATTR_TITLE     = "title";
    public static final String COLATTR_TOOLTIP   = "tooltip";
    public static final String COLATTR_TYPE      = "type";
    public static final String COLATTR_ENUMTYPE  = "enumType";

    public static final String COLATTR_MINLENGTH = "minLength";
    public static final String COLATTR_MINVALUE  = "minValue";
    public static final String COLATTR_MAXVALUE  = "maxValue";
    public static final String COLATTR_REGEXP    = "regExp";
    
    public static final String COLATTR_NUMBER_TYPE      = "numberType";             // "Integer", "Currency", "Percent"  
    public static final String COLATTR_NUMBER_GROUPSEP  = "numberGroupSeparator";   // boolean (true or false)
    public static final String COLATTR_FRACTION_DIGITS  = "numberFractionDigits";   // integer
    public static final String COLATTR_INTEGER_DIGITS   = "numberIntegerDigits";    // integer
    public static final String COLATTR_CURRENCY_CODE    = "currencyCode";           // "ISO 4217 code of the currency"
    public static final String COLATTR_DATETIMEPATTERN  = "dateTimePattern";        // default is yyyy-MM-dd HH:mm:ss

    /**
     * Returns the maximum size a value for this column is allowed to have.
     * <P>
     * For the data type DECIMAL the size defines the scale and precision of the value.
     * <P>
     * @return Returns the maximum size a value for this column is allowed to have.
     */
    double getSize();

    /**
     * Returns whether or not the value for this column must be
     * supplied (i.e. it is mandatory) or not.
     * <P>
     * @return Returns true if the value for this column must be supplied
     */
    boolean isRequired();

    /**
     * Returns whether or not the value for this column is auto-generated
     * <P>
     * @return Returns true if the value for this column is auto-generated
     */
    boolean isAutoGenerated();

    /**
     * Returns true if the values for this column are generally
     * read only (like i.e. for sequence generated values).
     * <P>
     * @return Returns true if the values for this column are generally read-only
     */
    boolean isReadOnly();

    /**
     * Checks if the given value is a valid value for this column 
     * If not, an exception is thrown
     * @return the value the validated and possibly converted value
     */
    Object validateValue(Object value);

}
