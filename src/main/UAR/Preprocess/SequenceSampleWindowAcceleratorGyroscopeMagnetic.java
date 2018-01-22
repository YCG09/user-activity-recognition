package com.lenovo.ca.UAR.Preprocess;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import com.lenovo.ca.UAR.Utils.TextPair;

public class SequenceSampleWindowAcceleratorGyroscopeMagnetic extends Configured implements Tool{
	private static final Logger logger = Logger.getLogger(SequenceSampleWindowAcceleratorGyroscopeMagnetic.class);
	static class sequence_sample_window_accelerator_gyroscope_magnetic_Mapper extends Mapper<LongWritable, Text, TextPair, Text> {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
			String activity_type = context.getConfiguration().get("activity_type");
			String line = value.toString().trim();
			String IMEI = "";
			String activity = "";
			String timestamp = "";
			double A_x_Axis = 0.0;
			double A_y_Axis = 0.0;
			double A_z_Axis = 0.0;
			double B_x_Axis = 0.0;
			double B_y_Axis = 0.0;
			double B_z_Axis = 0.0;
			double C_x_Axis = 0.0;
			double C_y_Axis = 0.0;
			double C_z_Axis = 0.0;
			double speed = -1.0;
			
        	if (null != line && !"".equals(line)) {
        		String[] cols = line.split("\t");
        		IMEI = cols[0];
        		activity = cols[1].toUpperCase();
        		timestamp = cols[2];
        		A_x_Axis = Double.parseDouble(cols[3]);
        		A_y_Axis = Double.parseDouble(cols[4]);
        		A_z_Axis = Double.parseDouble(cols[5]);
        		
        		B_x_Axis = Double.parseDouble(cols[6]);
        		B_y_Axis = Double.parseDouble(cols[7]);
        		B_z_Axis = Double.parseDouble(cols[8]);
        		
        		C_x_Axis = Double.parseDouble(cols[9]);
        		C_y_Axis = Double.parseDouble(cols[10]);
        		C_z_Axis = Double.parseDouble(cols[11]);
        		
        		speed = Double.parseDouble(cols[12]);
        		
    			if(activity_type.equals(activity) && "860988032536927".equals(IMEI) && timestamp != ""){
    				context.write(new TextPair(IMEI + "\t" + activity, timestamp), new Text(IMEI + "\t" + activity + "\t" + timestamp + "\t" + A_x_Axis + "\t" + A_y_Axis + "\t" + A_z_Axis + "\t" + B_x_Axis + "\t" + B_y_Axis + "\t" + B_z_Axis + "\t" + C_x_Axis + "\t" + C_y_Axis + "\t" + C_z_Axis + "\t" + speed));
    			}
    		}
		}
	}
	
	static class sequence_sample_window_accelerator_gyroscope_magnetic_Reducer extends Reducer<TextPair, Text, NullWritable, Text> {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		public void reduce(TextPair key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			Queue<String> queue = new LinkedList<String>();
			int window_length = 200;
			int step_length = 32;
			String line = "";
			String IMEI = key.getFirst().toString().split("\t")[0];
			String activity = key.getFirst().toString().split("\t")[1];
			Iterator<Text> inItr = values.iterator();
			String beginstr = null;
			String endstr = null;
			Date begin = null;
			Date end = null;
			long num = 0;
			while (inItr.hasNext()) {
				line = inItr.next().toString();
				if(beginstr == null){
					beginstr = line.split("\t")[2];
				}
				endstr = line.split("\t")[2];
				long between = 0;
				try {
					begin = dfs.parse(beginstr);
					end = dfs.parse(endstr);
					between = (end.getTime() - begin.getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(between < 60){
					queue.offer(line);
					beginstr = endstr;
				}else{
					Queue<String> window = new LinkedList<String>();
					int count = 0;
					int max_count =  (int) Math.floor((queue.size() - window_length) / step_length) - 1;
					while (!queue.isEmpty()) {
						String str = queue.poll();
						window.offer(str);
						if (window.size() == window_length) {
							ArrayList<String> list = new ArrayList<>();
							for(String s : window){
								list.add(s);
							}
//							long index = System.currentTimeMillis();
							HashSet<String> set = new HashSet<String>();
							for(int j = 0; j < window_length; j++){
								String[] cols = list.get(j).split("\t");
								set.add(cols[3] + "\t" + cols[4] + "\t" + cols[5] + "\t" + cols[6] + "\t" + cols[7] + "\t" + cols[8] + "\t" + cols[9] + "\t" + cols[10] + "\t" + cols[11]);
							}
							if(count < max_count - 1 && count > 1 && set.size() == window_length){
								for(int k = 0; k < window_length; k++){
									context.write(NullWritable.get(), new Text(list.get(k) + "\t" + IMEI + "_" + activity + "_" + num));
								}
								num++;
							}
							count++;
							for (int i = 0; i < step_length; i++) {
								window.poll();
							}
						}
					}
					queue.clear();
					beginstr = endstr;
					queue.offer(line);
					
				}
				
			}
			Queue<String> window = new LinkedList<String>();
			int count = 0;
			int max_count =  (int) Math.floor(queue.size() / (window_length - step_length)) - 1;
			while (!queue.isEmpty()) {
				String str = queue.poll();
				window.offer(str);
				if (window.size() == window_length) {
					ArrayList<String> list = new ArrayList<>();
					for(String s : window){
						list.add(s);
					}
					long index = System.currentTimeMillis();
					HashSet<String> set = new HashSet<String>();
					for(int j = 0; j < window_length; j++){
						String[] cols = list.get(j).split("\t");
						set.add(cols[3] + "\t" + cols[4] + "\t" + cols[5] + "\t" + cols[6] + "\t" + cols[7] + "\t" + cols[8] + "\t" + cols[9] + "\t" + cols[10] + "\t" + cols[11]);
					}
					if(count < max_count - 1 && count > 1 && set.size() == window_length){
						for(int k = 0; k < window_length; k++){
							context.write(NullWritable.get(), new Text(list.get(k) + "\t" + IMEI + "_" + activity + "_" + num));
						}
						num++;
					}
					count++;
					for (int i = 0; i < step_length; i++) {
						window.poll();
					}
				}
			}
				
		}
		
	}
	
	static class KeyPartitioner extends Partitioner<TextPair, Text> {
		public int getPartition(TextPair key, Text value, int numPartitions) {
			return (key.getFirst().hashCode() & Integer.MAX_VALUE) % numPartitions;
		}
	} 
	
	public static void main(String[] args) throws Exception{
		int exitCode = -1;
		if(3 == args.length){
			exitCode = ToolRunner.run(new SequenceSampleWindowAcceleratorGyroscopeMagnetic(), args);
		}else{
			System.err
			.println("Usage: sequence_sample_window_accelerator_gyroscope_magnetic <log_input_path> <output_path> <activity_type>");
			System.exit(-1);
		}
		System.exit(exitCode);
	}
	public int run(String[] args) throws Exception {
		Configuration curConf = getConf();
		String activityType = args[2];
		curConf.set("activity_type", activityType);
		Job job =   Job.getInstance(curConf,"sequence_sample_window_accelerator_gyroscope_magnetic");
		job.setJarByClass(SequenceSampleWindowAcceleratorGyroscopeMagnetic.class);

		String inputPath = args[0];
		String outputPath = args[1];

		FileSystem fs = FileSystem.get(curConf);
		Path path = new Path(outputPath);
		if (fs.exists(path)) {
			fs.delete(path, true);
		}

		FileInputFormat.setInputPaths(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.setMapperClass(sequence_sample_window_accelerator_gyroscope_magnetic_Mapper.class);
		job.setReducerClass(sequence_sample_window_accelerator_gyroscope_magnetic_Reducer.class);
		job.setPartitionerClass(KeyPartitioner.class);
		job.setGroupingComparatorClass(TextPair.FirstComparator.class);
		// set map's key and value format
		job.setMapOutputKeyClass(TextPair.class);
		job.setMapOutputValueClass(Text.class);

		// set reduce's key and value format
		job.setNumReduceTasks(Integer.parseInt("1"));
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);

		return job.waitForCompletion(true) ? 0 : 1;
	}

}
