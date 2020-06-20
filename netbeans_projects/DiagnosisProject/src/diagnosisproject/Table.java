/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package diagnosisproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Istvan Sarandi
 */
public class Table
{
    List<List<Object>> table = new ArrayList<List<Object>>();

    public void setAll(int rstart, int cstart, List<? extends List<?>> content)
    {
        int r = rstart;
        int c = cstart;
        
        for (List<?> row : content)
        {
            c = cstart;
            for (Object cell : row)
            {
                set(r, c, cell);
                ++c;
            }
            ++r;
        }
    }

    public void set(int r, int c, Object content)
    {
        while (table.size() < r + 1)
        {
            table.add(new ArrayList<Object>());
        }

        List<Object> row = table.get(r);

        while (row.size() < c + 1)
        {
            row.add("");
        }
        row.set(c, content);
        
    }

    public void setAllHoriz(int r, int c, List<?> contents, int step)
    {
        int i = 0;
        for (Object content : contents)
        {
            set(r, c + i, content);

            i += step;
        }
    }

    public void setAllHoriz(int r, int c, List<?> contents)
    {
        setAllHoriz(r, c, contents, 1);
    }

    public void setAllVert(int r, int c, List<?> contents)
    {
        setAllVert(r, c, contents, 1);
    }

    public void setAllVert(int r, int c, List<?> contents, int step)
    {
        int i = 0;
        for (Object content : contents)
        {
            set(r + i, c, content);

            i += step;
        }
    }
    
    private String contentToString(Object content)
    {
        if (content instanceof Double)
        {
            return String.format(Locale.forLanguageTag("hu-HU"), "%f", content);
        } else
        {
            return content.toString();
        }
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (List<Object> row : table)
        {
            for (Object cell : row)
            {
                sb.append(contentToString(cell)).append("; ");
            }
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }
}