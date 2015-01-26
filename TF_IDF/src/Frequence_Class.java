package osj.freq;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by osujin on 15. 1. 27..
 */
public class Frequence_Class extends Configured implements Tool{
    public static void main(String[] args) throws Exception {

        if(args.length != 2){
            System.out.println("check input / output directory!!!");
            System.exit(-1);
        }

        int run = ToolRunner.run(new Configuration(),new Frequence_Class(), args);
        System.exit(run);
    }

    public int run(String[] args) throws Exception{
        Configuration conf = getConf();
        Job job = new Job(conf,"word frequence Document");

        job.setJarByClass(Frequence_Class.class);
        job.setMapperClass(Freq_Mapper.class);
        job.setReducerClass(Freq_Reducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static class Freq_Mapper extends Mapper<LongWritable,Text,Text,IntWritable>{
        private static final Pattern PATTERN = Pattern.compile("\\w+");
        public static final IntWritable one = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            FileSplit split = (FileSplit) context.getInputSplit();
            String DOC_Name = split.getPath().getName();

            Matcher matcher = PATTERN.matcher(value.toString());
            while (matcher.find()){
                String word = matcher.group().toLowerCase();
                context.write(new Text(word+" "+DOC_Name),one);
            }
        }
    }

    public static class Freq_Reducer extends Reducer<Text,IntWritable, Text, IntWritable>{
        private IntWritable intWritable = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int total = 0;
            for(IntWritable text : values){
                total += text.get();
            }
            intWritable.set(total);
            context.write(key,intWritable);
        }
    }
}
