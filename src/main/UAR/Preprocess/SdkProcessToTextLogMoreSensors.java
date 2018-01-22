package com.lenovo.ca.UAR.Preprocess;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

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

//import org.apache.log4j.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SdkProcessToTextLogMoreSensors extends Configured implements Tool{
	private static final Logger logger = Logger.getLogger( SdkProcessToTextLogMoreSensors.class);
	static class  sdk_process_to_text_log_more_sensors_Mapper extends Mapper<LongWritable, Text, TextPair, Text> {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
			String line = value.toString().trim();
        	if (null != line && !"".equals(line)) {
        		JSONArray jsonArr = JSONArray.fromObject(line);
        		Iterator<Object> iter = jsonArr.iterator();
        		while (iter.hasNext()) {
    				double accelerator_x_Axis = 0.0;
        			double accelerator_y_Axis = 0.0;
        			double accelerator_z_Axis = 0.0;
        			double gyroscope_x_Axis = 0.0;
        			double gyroscope_y_Axis = 0.0;
        			double gyroscope_z_Axis = 0.0;
        			double magnetic_x_Axis = 0.0;
        			double magnetic_y_Axis = 0.0;
        			double magnetic_z_Axis = 0.0;
        			
        			JSONObject A_data = null;
        			JSONObject B_data = null;
        			JSONObject C_data = null;
        			
        			String activity = "";
        			String IMEI = "";
        			String timestamp = null;
        			JSONObject obj = (JSONObject) iter.next();
        			logger.info("obj is ----------------" + obj.toString());
        			if(obj.keySet().contains("IMEI")){
        				IMEI = obj.getString("IMEI");
        			}
        			
        			if(obj.keySet().contains("time")){
        				timestamp = df.format(new Date(obj.getLong("time")));
        			}
    				
        			if(obj.keySet().contains("activity")){
        				activity = obj.getString("activity");
        			}
        			if(obj.keySet().contains("world_liner_acceleration") ){
        				A_data = obj.getJSONObject("world_liner_acceleration");
        				accelerator_x_Axis = A_data.getDouble("x_Axis");
        				accelerator_y_Axis = A_data.getDouble("y_Axis");
        				accelerator_z_Axis = A_data.getDouble("z_Axis");
        			}
        			if(obj.keySet().contains("gyroscope") ){
        				B_data = obj.getJSONObject("gyroscope");
        				gyroscope_x_Axis = B_data.getDouble("x_Axis");
        				gyroscope_y_Axis = B_data.getDouble("y_Axis");
        				gyroscope_z_Axis = B_data.getDouble("z_Axis");
        			}
        			if(obj.keySet().contains("magnetic_field") ){
        				C_data = obj.getJSONObject("magnetic_field");
        				magnetic_x_Axis = C_data.getDouble("x_Axis");
        				magnetic_y_Axis = C_data.getDouble("y_Axis");
        				magnetic_z_Axis = C_data.getDouble("z_Axis");
        			}
    				if(activity != "" && activity.length() != 0 && A_data != null && B_data != null && C_data != null){
    					context.write(new TextPair(IMEI + "\t" + activity, timestamp), new Text(IMEI + "\t" + activity + "\t" + timestamp + "\t" + accelerator_x_Axis + "\t" + accelerator_y_Axis + "\t" + accelerator_z_Axis + "\t" + gyroscope_x_Axis + "\t" + gyroscope_y_Axis + "\t" + gyroscope_z_Axis + "\t" + magnetic_x_Axis + "\t" + magnetic_y_Axis + "\t" + magnetic_z_Axis));
    				}

        		}
    			
    		}
		}
	}
	
	static class  sdk_process_to_text_log_more_sensors_Reducer extends Reducer<TextPair, Text, NullWritable, Text> {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		public void reduce(TextPair key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String line = "";
			Iterator<Text> inItr = values.iterator();
			String beginstr = null;
			String endstr = null;
			Date begin = null;
			Date end = null;
			
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
				beginstr = endstr;
				context.write(NullWritable.get(), new Text(line + "\t" + between));
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
		if(2 == args.length){
			exitCode = ToolRunner.run(new SdkProcessToTextLogMoreSensors(), args);
		}else{
			System.err
			.println("Usage: sdk_process_to_text_log_more_sensors <log_input_path> <output_path>");
			System.exit(-1);
		}
		System.exit(exitCode);
	}
	public int run(String[] args) throws Exception {
		Configuration curConf = getConf();
		Job job =   Job.getInstance(curConf,"test");
		job.setJarByClass(SdkProcessToTextLogMoreSensors.class);

		String inputPath = args[0];
		String outputPath = args[1];

		FileSystem fs = FileSystem.get(curConf);
		Path path = new Path(outputPath);
		if (fs.exists(path)) {
			fs.delete(path, true);
		}

		FileInputFormat.setInputPaths(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.setMapperClass(sdk_process_to_text_log_more_sensors_Mapper.class);
		job.setReducerClass(sdk_process_to_text_log_more_sensors_Reducer.class);
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
