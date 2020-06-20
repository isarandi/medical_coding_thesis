## BSc Thesis Supplementary Materials

István Sárándi: Developing a System to Support Medical Coding (Egészségügyi Kódolástámogató Rendszer Fejlesztése)

Contents:

- batch_files:
	example commands to run the program

- classifier_architectures:
	XML markup for the evaluated classifier architectures

- netbeans_projects:
	Project directories for the NetBeans IDE. The /src subfolder has the source code, /dist the .jar files

------------------------------

## Interpreting the output

The output of an evaluation is a sequence of values: R(1), R(5), R(10), R(20)
that is, the top-k accuracy values for different k.

Interpreting the cross-validation result:
First line has the overall averages, the other lines show the results of the individial folds.
Columns: R(1), R(5), R(10), R(20)

More complex evaluations require modifying the sources.

------------------------------

Bringing up the web interface

Load the webserver project in NetBeans and start it, the browser will open automatically.
It will try to connect to the classifier server at localhost:5555.
(I tested it with the GlassFish webserver, which can be installed in a perfectly self-configuring and trivial way within NetBeans.

Have fun!

István Sárándi, 2011-12-09.
