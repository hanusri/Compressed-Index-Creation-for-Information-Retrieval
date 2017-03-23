Instruction to run the application
----------------------------------

After navigating to root directory of the project, run the following command
	source /usr/local/corenlp350/classpath.sh



Below is the script to compile and run the application

javac -cp $CLASSPATH ApplicationRunner.java
java ApplicationRunner ../Dataset ../stopwords.txt

Following are the understanding about the above script

1.	ApplicationRunner.java has the main method as starting point
2.	While executing, the application expects two parameter as argument 
		args0- Path where the set of documents to be indexed are available. In the above example, ../Dataset is passed as argument. This means the folder Dataset has the collection of documents to be indexed. The folder is residing one step back to the folder where ApplicationRunner.java is residing. However, if dataset is available in the path /people/cs/s/sanda/cs6322/Cranfield, then following is the command to be executed

		java ApplicationRunner /people/cs/s/sanda/cs6322/Cranfield ../stopwords.txt

		args1- Path where list of stopwords are available. I have attached stopwords file along with the project. Please copy it and refer the path as second argument of the application.


Following are some important point to be noted as part of the execution

1.	While executing the application, initially lot of log information gets printed related to lemmatization. As standford NLP lemmatization method is called it prints lot of data. Please wait a while  (20 to 30 seconds) before expected output starts printing
2.	All document IDs printed are zero indexed