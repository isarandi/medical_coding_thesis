java -jar ..\netbeans_projects\DiagnosisProject\dist\DiagnosisProject.jar ^
 crossval ..\classifier_architectures\svm_moe_perceptron_moe_mix.xml ^
          ..\samples_german\de_morpho.txt ^
          5 2 ^
          .\output\german_results.csv