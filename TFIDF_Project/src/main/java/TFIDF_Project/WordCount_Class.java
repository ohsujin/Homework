package TFIDF_Project;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by osujin on 15. 1. 27..
 */
public class WordCount_Class extends Configured implements Tool {

    public static void main(String[] args) throws Exception {

	try {
            System.out.println("Input directory is ["+args[0]+"]");
        }catch (Exception e) {
            System.err.println("input directory Error!!");
        }

        try {
            System.out.println("Output directory is ["+args[1]+"]");
        }catch (Exception e) {
            System.err.println("Output directory Error!!");
        }
	
        int run = ToolRunner.run(new Configuration(), new WordCount_Class(), args);
        System.exit(run);
    }

    @Override
    public int run(String[] strings) throws Exception {
        Configuration configuration = getConf();
        Job job = new Job(configuration,"Word_Count");

        job.setJarByClass(WordCount_Class.class);
        job.setMapperClass(WordCount_Mapper.class);
        job.setReducerClass(WordCount_Reducer.class);

//        job.setMapOutputValueClass();  //출력 형식을 바꿔주는 또 다른 방법
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(strings[0]));
        FileOutputFormat.setOutputPath(job, new Path(strings[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class WordCount_Mapper extends Mapper<LongWritable,Text,Text,Text>{

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] fre_input = value.toString().split("\\s+");
            String term = fre_input[0];
            String DOC_Name = fre_input[1];

            int frequence = Integer.parseInt(fre_input[2]);

            context.write(new Text(term),new Text(DOC_Name+ " "+frequence));
        }
    }

    public static class WordCount_Reducer extends Reducer<Text,Text,Text,Text>{

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Map<Text,String> count = new HashMap<Text, String>();
            int total = 0;

            for (Text word : values){
                String[] DOC_Name_tf = word.toString().split("\\s+");
                String DOC_Name = DOC_Name_tf[0];
                String tf_value = DOC_Name_tf[1];

                count.put(new Text(key.toString()+" "+DOC_Name),tf_value);
                total ++;
            }

            for (Text DOC_Name : count.keySet()){
                String tf = count.get(DOC_Name);
                context.write(DOC_Name,new Text(tf+" "+total));
            }
        }

    }

}

