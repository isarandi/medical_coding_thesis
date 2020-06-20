package framework;

public class Result<Out> {

    private Out output;
    private double confidence;

    public Result(Out output, double confidence)
    {
        this.output = output;
        this.confidence = confidence;
    }

    public double getConfidence()
    {
        return confidence;
    }

    public void setConfidence(double confidence)
    {
        this.confidence = confidence;
    }

    public Out getOutput()
    {
        return output;
    }

    public void setOutput(Out output)
    {
        this.output = output;
    }
}
