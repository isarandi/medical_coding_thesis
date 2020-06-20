/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package framework.adaption;

import framework.Result;
import framework.ResultSet;
import framework.ResultSet.ResultBlender;

/**
 *
 * @author Istvan Sarandi
 */
public class ResultSetConverter<OutFrom,OutTo> implements Converter<ResultSet<OutFrom>,ResultSet<OutTo>>
{
    Converter<OutFrom, OutTo> innerConverter;
    ResultBlender rb;

    public ResultSetConverter(Converter<OutFrom, OutTo> innerConverter, ResultBlender rb)
    {
        this.innerConverter = innerConverter;
        this.rb = rb;
    }
    
    public ResultSet<OutTo> convert(ResultSet<OutFrom> input)
    {
        ResultSet<OutTo> converted = new ResultSet<OutTo>(input.getLimit());
        for (Result<OutFrom> r : input)
        {
            converted.push(innerConverter.convert(r.getOutput()), r.getConfidence(), rb);
        }
        return converted;
    }
    
}
