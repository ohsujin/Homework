package TF_IDF_Class;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by osujin on 15. 1. 27..
 */
public class TF_IDF_Class extends Configured implements Tool {

    public static void main(String[] args) throws Exception {

        if(args.length != 2){
            System.out.println("check input/output directory!!!");
            System.exit(-1);
        }

        int run = ToolRunner.run(new Configuration(), new TF_IDF_Class(), args);
        System.exit(run);
    }

    @Override
    public int run(String[] strings) throws Exception {
        Configuration conf = new Configuration();
        FileSystem file = FileSystem.get(conf);

        FileStatus[] liststat = file.listStatus(new Path(strings[0]));
        conf.setInt("N",liststat.length);

        Job job = new Job(conf,"TF_IDF of wordcount");

        job.setJarByClass(TF_IDF_Class.class);
        job.setMapperClass(TF_IDF_Mapper.class);
        job.setReducerClass(TF_IDF_Reducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(strings[0]));
        FileOutputFormat.setOutputPath(job, new Path(strings[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }
    public static class TF_IDF_Mapper extends Mapper<LongWritable, Text, Text, Text> {
        private static int N;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            N = conf.getInt("N",0);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] field = value.toString().split("\\s+");
            String text = field[0];
            String DOC_Name = field[1];
            int TF = Integer.parseInt(field[2]);
            int n = Integer.parseInt(field[3]);

            double IDF = Math.log(N/n);
            double TF_IDF = TF * IDF;

            context.write(new Text(text+" => "+DOC_Name),new Text(String.valueOf(TF_IDF)));
        }


    }

    public static class TF_IDF_Reducer extends Reducer<Text, Text, Text, Text>{
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            for (Text word : values){
                context.write(key,word);
            }

        }
    }

}
