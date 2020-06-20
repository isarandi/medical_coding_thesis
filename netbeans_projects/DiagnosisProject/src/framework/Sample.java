package framework;

public class Sample<In, Out> {

    private In input;

    public void setInput(In input)
    {
        this.input = input;
    }

    public void setOutput(Out output)
    {
        this.output = output;
    }
    private Out output;

    public In getInput()
    {
        return input;
    }

    public Out getOutput()
    {
        return output;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sample<In, Out> other = (Sample<In, Out>) obj;
        if (this.input != other.input && (this.input == null || !this.input.equals(other.input))) {
            return false;
        }
        if (this.output != other.output && (this.output == null || !this.output.equals(other.output))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 59 * hash + (this.input != null ? this.input.hashCode() : 0);
        hash = 59 * hash + (this.output != null ? this.output.hashCode() : 0);
        return hash;
    }

    public Sample(In in, Out out)
    {
        input = in;
        output = out;
    }
}
