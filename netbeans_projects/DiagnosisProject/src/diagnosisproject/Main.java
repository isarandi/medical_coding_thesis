package diagnosisproject;

import io.FileProcessor;
import io.xml.XMLClassifierReader;
import framework.Classifier;

import framework.SampleSet;
import framework.evaluation.PositionAtMostMetric;
import framework.evaluation.EvaluationMetric;
import framework.evaluation.Evaluator;
import io.ObjectPersistance;
import io.networking.ClassificationService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import xmlprocnstream.XMLParseException;

public class Main
{
    static EvaluationMetric[] vms =
    {
        new PositionAtMostMetric(1), new PositionAtMostMetric(5), new PositionAtMostMetric(10), new PositionAtMostMetric(20)
    };

    public static void main(String[] args)
    {
        System.out.println("");
        System.out.println("-- Welcome to the Medical Coding Framework --");
        System.out.println("");

        if (args.length == 0)
        {
            helpinfo();
            return;
        }

        if (args[0].equals("train"))
        {
            System.out.println("Training mode.");
            train(args);
        } else if (args[0].equals("test"))
        {
            System.out.println("Testing mode.");
            test(args);
        } else if (args[0].equals("serve"))
        {
            System.out.println("Server mode.");
            serve(args);
        } else if (args[0].equals("traintest"))
        {
            System.out.println("Training and testing mode.");
            traintest(args);
        } else if (args[0].equals("crossval"))
        {
            System.out.println("Cross-validation mode.");
            crossValidate(args);
        } 

    }

    private static void helpinfo()
    {
        System.out.println("The application can be run in several modes.");
        System.out.println("The required arguments are as follows, respectively.\n");

        System.out.println("1. Training: train architecture_file training_set_file output_classifier_file");
        System.out.println("2. Testing: test classifier_file test_set_file");
        System.out.println("3. Cross-validating: crossval architecture_file sample_set_file num_of_cv_parts execute_cv_parts [output_results_file]");
        System.out.println("4. Training & testing: traintest architecture_file training_set_file test_set_file");
        System.out.println("5. Server mode: serve classifier_file port_number");
    }

    private static void train(String[] args)
    {
        Classifier<String, String> c = createClassifier(args[1], args[2]);
        ObjectPersistance.export(c, args[3]);
    }

    private static void traintest(String[] args)
    {
        Classifier<String, String> c = createClassifier(args[1], args[2]);
        test(c, args[3]);
    }

    private static void test(String[] args)
    {
        Classifier<String, String> c =
                (Classifier<String, String>) ObjectPersistance.import_(args[1]);
        test(c, args[2]);
    }

    private static void serve(String[] args)
    {
        System.out.println("Deserializing classifier from: " + args[1]);
        Classifier<String, String> c =
                (Classifier<String, String>) ObjectPersistance.import_(args[1]);

        ClassificationService service = new ClassificationService(c);

        System.out.println("Starting listening on port: " + args[2]);
        service.listenSocket(Integer.parseInt(args[2]));
    }

    private static Classifier<String, String> createClassifier(String classifierpath,
                                                               String trainingpath)
    {
        System.out.println("Importing training samples from: " + trainingpath);
        SampleSet<String, String> training =
                FileProcessor.readSampleSetFromFile(trainingpath);

        System.out.println("Parsing architecture from: " + classifierpath);

        Classifier<String, String> c = null;
        try
        {
            c = XMLClassifierReader.readClassifier(classifierpath);
        } catch (XMLParseException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Training classifier...");
        c.train(training);
        return c;
    }

    private static void test(Classifier<String, String> c, String testpath)
    {
        System.out.println("Importing test samples from: " + testpath);

        SampleSet<String, String> testSet =
                FileProcessor.readSampleSetFromFile(testpath);

        System.out.println("Testing classifier...");
        List<Double> values = Evaluator.validate(c, testSet, vms);

        System.out.println("Test results:");
        for (double val : values)
        {
            System.out.println(val);
        }
    }

    private static void crossValidate(String[] args)
    {
        System.out.println("Importing samples from: " + args[2]);
        SampleSet<String, String> samples =
                FileProcessor.readSampleSetFromFile(args[2]);

        System.out.println("Parsing architecture from: " + args[1]);

        Classifier<String, String> c = null;
        try
        {
            c = XMLClassifierReader.readClassifier(args[1]);
        } catch (XMLParseException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Cross-validating classifier...");
        List<List<Double>> successMatrix = Evaluator.crossValidate(c,
                                                                   samples,
                                                                   Integer.parseInt(args[3]),
                                                                   Integer.parseInt(args[4]),
                                                                   vms);

        Table table = new Table();
        table.setAll(0, 0, successMatrix);

        if (args.length > 5)
        {
            String outputfile = args[5];
            FileProcessor.writeToFile(table.toString(), outputfile);
        } else
        {
            System.out.println(table.toString());
        }
    }

}
