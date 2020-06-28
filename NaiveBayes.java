// Do not submit with package statements if you are using eclipse.
// Only use what is provided in the standard libraries.

import java.io.*;
import java.util.*;

public class NaiveBayes {

    /**
     * The double value for probability fo getting a Spam email
     */
    private double probabilitySpam;

    /**
     * The double value for probability fo getting a Ham email
     */
    private double probabilityHam;

    /**
     * The word in the training data and how many times it has been
     * seen in a Spam email.
     */
    private HashMap<String, Double> wordsCountInSpam;

    /**
     * The word in the training data and how many times it has been
     * seen in a Ham email.
     */
    private HashMap<String, Double> wordsCountInHam;

    /**
     * All the words in the training data, both in spam and ham emails.
     */
    private HashSet<String> words;

    /*
     * !! DO NOT CHANGE METHOD HEADER !!
     * If you change the method header here, our grading script won't
     * work and you will lose points!
     * 
     * Train your Naive Bayes Classifier based on the given training
     * ham and spam emails.
     *
     * Params:
     *      hams - email files labeled as 'ham'
     *      spams - email files labeled as 'spam'
     */
    public void train(File[] hams, File[] spams) throws IOException {
        words = new HashSet<>();

        //Calculating the probability of spam and ham email
        probabilitySpam = ((double) spams.length) / (double) (spams.length + hams.length);
        probabilityHam = ((double) hams.length) / (double) (spams.length + hams.length);
        wordsCountInSpam = new HashMap<>();
        wordsCountInHam = new HashMap<>();

        //Adding all the words in the training set to my HashSet. And counting
        //how many times the words are seen in each type of email.
        HashSet<String> filler;
        for (File spam : spams) {
            filler = tokenSet(spam);
            for (String w : filler){
                if(!words.contains(w)) {
                    words.add(w);
                }
                if(wordsCountInSpam.containsKey(w)){
                    double count = wordsCountInSpam.get(w).doubleValue();
                    count = count + 1.0;
                    wordsCountInSpam.replace(w, count);
                } else {
                    double count = 1.0;
                    wordsCountInSpam.put(w, count);
                }
            }
        }
        for (File ham : hams) {
            filler = tokenSet(ham);
            for (String w : filler){
                if(!words.contains(w)) {
                    words.add(w);
                }
                if(wordsCountInHam.containsKey(w)){
                    double count = wordsCountInHam.get(w).doubleValue();
                    count = count + 1.0;
                    wordsCountInHam.replace(w, count);
                } else {
                    double count = 1.0;
                    wordsCountInHam.put(w, count);
                }
            }
        }
        //Now we go through each word and calculate the probability of
        //that word being seen in each type of email.
        for(String word : words){
            double probability;
            if(wordsCountInSpam.containsKey(word)) {
                double c = wordsCountInSpam.get(word).doubleValue();
                probability = (c + 1.0) / (spams.length + 2.0);
                wordsCountInSpam.replace(word, probability);
            } else {
                probability = 1.0 / (spams.length + 2.0);
                wordsCountInSpam.put(word, probability);
            }
            if(wordsCountInHam.containsKey(word)){
                double c = wordsCountInHam.get(word).doubleValue();
                probability = (c + 1.0) / (hams.length + 2.0);
                wordsCountInHam.replace(word, probability);
            } else {
                probability = 1.0 / (hams.length + 2.0);
                wordsCountInHam.put(word, probability);
            }
        }
    }

    /*
     * !! DO NOT CHANGE METHOD HEADER !!
     * If you change the method header here, our grading script won't
     * work and you will lose points!
     *
     * Classify the given unlabeled set of emails. Add each email to the correct
     * label set. SpamFilterMain.java would follow the format in
     * example_output.txt and output your result to stdout. Note the order
     * of the emails in the output does NOT matter.
     * 
     *
     * Params:
     *      emails - unlabeled email files to be classified
     *      spams  - set for spam emails that needs to be populated
     *      hams   - set for ham emails that needs to be populated
     */
    public void classify(File[] emails, Set<File> spams, Set<File> hams) throws IOException {
        //Taking the log(P(S)) and log(P(H))
        double probF = Math.log(probabilitySpam);
        double probH = Math.log(probabilityHam);
        double sumH = 0.0;
        double sumF = 0.0;

        //For each email, I add up the probabilities of each word that matches the
        //words in the data set to then calculate the probability of it being a spam
        //email.
        for (File email : emails) {
            sumH = 0.0;
            sumF = 0.0;
            HashSet<String> xs = tokenSet(email);
            for(String x : xs){
                if(words.contains(x)){
                    sumH = sumH + Math.log(wordsCountInHam.get(x));
                    sumF = sumF + Math.log(wordsCountInSpam.get(x));
                }
            }
            if ((probF + sumF) > (probH + sumH)){
                spams.add(email);
            } else {
                hams.add(email);
            }
        }
    }


    /*
     *  Helper Function:
     *  This function reads in a file and returns a set of all the tokens. 
     *  It ignores "Subject:" in the subject line.
     *  
     *  If the email had the following content:
     *  
     *  Subject: Get rid of your student loans
     *  Hi there ,
     *  If you work for us , we will give you money
     *  to repay your student loans . You will be 
     *  debt free !
     *  FakePerson_22393
     *  
     *  This function would return to you
     *  ['be', 'student', 'for', 'your', 'rid', 'we', 'of', 'free', 'you', 
     *   'us', 'Hi', 'give', '!', 'repay', 'will', 'loans', 'work', 
     *   'FakePerson_22393', ',', '.', 'money', 'Get', 'there', 'to', 'If', 
     *   'debt', 'You']
     */
    public static HashSet<String> tokenSet(File filename) throws IOException {
        HashSet<String> tokens = new HashSet<String>();
        Scanner filescan = new Scanner(filename);
        filescan.next(); // Ignoring "Subject"
        while(filescan.hasNextLine() && filescan.hasNext()) {
            tokens.add(filescan.next());
        }
        filescan.close();
        return tokens;
    }
}
