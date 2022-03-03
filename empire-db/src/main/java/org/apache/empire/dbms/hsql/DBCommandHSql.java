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
package org.apache.empire.dbms.hsql;

import java.util.ArrayList;
import java.util.List;

import org.apache.empire.data.DataType;
import org.apache.empire.db.DBColumn;
import org.apache.empire.db.DBColumnExpr;
import org.apache.empire.db.DBCommand;
import org.apache.empire.db.DBRowSet;
import org.apache.empire.db.exceptions.NoPrimaryKeyException;
import org.apache.empire.db.expr.column.DBAliasExpr;
import org.apache.empire.db.expr.column.DBValueExpr;
import org.apache.empire.db.expr.set.DBSetExpr;

/**
 * This class handles the special features of an HSqlDB database.
 */
public class DBCommandHSql extends DBCommand
{
    // *Deprecated* private static final long serialVersionUID = 1L;
  
    protected int limitRows = -1;
    protected int skipRows  =  0;

    /**
     * Constructs an HSqlDB command object.
     */
    public DBCommandHSql(boolean autoPrepareStmt)
    {
        super(autoPrepareStmt);
    }

    @Override
    public DBCommandHSql limitRows(int limitRows)
    {
        // set limit
        this.limitRows = limitRows;
        return this;
    }

    @Override
    public DBCommandHSql skipRows(int skipRows)
    {
        // set skip
        this.skipRows = skipRows;
        return this;
    }
     
    @Override
    public void clearLimit()
    {
    	// remove skip and limit
        this.limitRows = -1;
        this.skipRows  =  0;
    }
        
    @Override
    public void getSelect(StringBuilder buf)
    {   // call base class
        super.getSelect(buf);
        // add limit and offset
        if (limitRows>=0)
        {   buf.append("\r\nLIMIT ");
            buf.append(String.valueOf(limitRows));
            // Offset
            if (skipRows>0) 
            {   buf.append(" OFFSET ");
                buf.append(String.valueOf(skipRows));
            }    
        }
    }

    @Override
    protected void addUpdateWithJoins(StringBuilder buf, DBRowSet table)
    {
        // The update table
        DBColumn[] keyColumns = table.getKeyColumns();
        if (keyColumns==null || keyColumns.length==0)
            throw new NoPrimaryKeyException(table);
        // Generate Merge expression
        buf.setLength(0);
        buf.append("MERGE INTO ");
        table.addSQL(buf, CTX_FULLNAME|CTX_ALIAS);
        // Using
        buf.append("\r\nUSING (");
        // Add set expressions
        List<DBColumnExpr> using = new ArrayList<DBColumnExpr>();
        // Add key columns
        for (DBColumn col : keyColumns)
            using.add(col);
        // Select Set-Expressions
        List<DBSetExpr> mergeSet = new ArrayList<DBSetExpr>(set.size());   
        for (DBSetExpr sex : set)
        {   // Select set expressions
            Object val = sex.getValue();
            if (val instanceof DBColumnExpr)
            {
                DBColumnExpr expr = ((DBColumnExpr)val);
                if (!(expr instanceof DBColumn) && !(expr instanceof DBAliasExpr))
                {   // rename column
                    String name = "COL_"+String.valueOf(mergeSet.size());
                    expr = expr.as(name);
                }
                // select
                using.add(expr);
                // Name
                DBValueExpr NAME_EXPR = getDatabase().getValueExpr("q0."+expr.getName(), DataType.UNKNOWN);
                mergeSet.add(sex.getColumn().to(NAME_EXPR));
            }
            else
            {   // add original
                mergeSet.add(sex);
            }
        }
        // Add select
        buf.append("SELECT ");
        addListExpr(buf, using, CTX_ALL, ", ");
        // From clause
        addFrom(buf);
        // Add Where
        addWhere(buf);
        // Add Grouping
        addGrouping(buf);
        // on
        buf.append(") q0\r\nON (");
        for (DBColumn col : keyColumns)
        {   // compare 
            buf.append(" q0.");
            col.addSQL(buf, CTX_NAME);
            buf.append("=");
            buf.append(table.getAlias());
            buf.append(".");
            col.addSQL(buf, CTX_NAME);
        }
        // Set Expressions
        buf.append(")\r\nWHEN MATCHED THEN UPDATE ");
        buf.append("\r\nSET ");
        addListExpr(buf, mergeSet, CTX_DEFAULT, ", ");
    }
}
