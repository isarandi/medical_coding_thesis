java -jar ..\netbeans_projects\DiagnosisProject\dist\DiagnosisProject.jar ^
 crossval ..\classifier_architectures\svm_moe_perceptron_moe_mix.xml ^
          ..\samples_hungarian\hu.txt ^
          5 5 ^
          .\output\moe_moe_results.csv