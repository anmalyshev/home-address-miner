Home Address Miner Application

   This test application tries to guess device home address based on the data provided in csv file.

Algorithm description.

   2 different algorithms were implemented:

"Classic" algorithm.

   This one is not fully map reduce, but it seems like it provides better results in some scenarios (for devices which are at home very rarely). It requires more communication between the cluster machines so that particular machine can process all the data for particular device. Not sure if it's possible to implement it fully with map-reduce approach.

   2 assumptions were made based on manual analysis of the data for few devices:
   1) Device owners do sleep. They typically do sleep at home. And device is not used during this time - meaning no data for the period. So, if we extract coordinates just before and right after the sleep period we may consider these coordinates to be a home address coordinate candidates.
   2) Device owner spends some time at home (some time on weekdays and a lot of time on weekends) and device is located at nearly the same place during this period. Meaning that there will probably be a lot of points close to home coordinates overall.

   Also looking at real data it turns out that day cycle is completely different for different device owners, so it's problematic to take into consideration device time zone.

   Based on that the following algorithm was designed:
   1) Filter empty lines and header from input data.
   2) Aggregate data by device ID and sort by timestamp. This will allow to break work between the cluster machines.
   3) Locate points which have meaningful (3-24 hours) period between them and cluster them. Get 5 clusters with highest weight. Cluster mass centers will be the candidates for home location. Clustering is limited to max distance.
   4) Cluster all the points around these candidates. Clustering is limited to max distance. Get cluster with highest weight and consider it's mass center as home coordinates.

   Let's a assume M points total. D devices with average number of points P. M = D * P. We also need to assume that average number of "candidate points" for each device is X.
   Algorithm complexity is M*log(P) or M * X - depends on which one is higher.

"MapReduce" algorithm.

   This one is fully map reduce. In most cases both algorithms provide close results, but in some cases they are different. Analyzing a few results which are different manually shown that "Classic" algorithm behaves better. However, it's obvious that "MapReduce" algorithm has much better performance.

   Again, 2 assumptions were made:
   1) Typically device owner is at work during day time and at home during night time. Morning and evening are undefined - depends on device owner life style.
   2) Device owner spends some time at home (some time on weekdays and a lot of time on weekends) and device is located at nearly the same place during this period. Meaning that there will probably be a lot of points close to home coordinates overall.

   Based on that the following algorithm was designed:
   1) Filter empty lines and header from input data.
   2) Associate "weight" with each point (based on timestamp - max weight for night hours and min for day hours) and reduce by (device_id, x, y) key.
   3) Map result to have device_id as a key and x, y and weight as value and reduce by key the way that only a point with max weight for each device is left.
   4) Return above data as a result.

   Algorithm complexity is O(n) as all the operations used (map and reduceByKey) are O(n) (not counting sorting in main class as it's for convenience only)
   
Algorithms improvements.
   One thing which can be done to improve algorithms is to associate weights more precisely based on data analysis. Currently weights are just manually set to some "logical" values.
   WiFi connection info would make it possible to define home coordinates more precisely (as, if device connects to the same WiFi regularly there's high probability that it connects at home). Similarly the information if device is chanrging would help.
   It would also be good to have point coordinates measurement error - this way we can filter out points which have high measurement error. Not sure if it'll make results better though as such points wold typically get small weight anyway.
   Also it makes sense to analyze device routes to separate typical routes and try to retrieve home coordinates out of this data. Requires data for larger period of time.

Running the application (includes build, test and execute).

   On both platforms scripts are assuming that Maven is installed and available in path. Also, "hdfs" command should be available in path.
   HDFS version used by the application is 2.7.2. Should be compatible across major release though.

   Windows:
   1) Modify properties.bat file according to your environment.
   2) Run run.bat script.

   Linux:
   1) Modify properties.sh file according to your environment.
   2) Run run.sh script.

   Both "Classic" and "MapReduce" algorithms are executed.
   Results for "Classic" algorithm are retrieved from HDFS to ./results_classic.csv file in the root of the project.
   Results for "MapReduce" algorithm are retrieved from HDFS to ./results_mapreduce.csv file in the root of the project.

   Note that Linux scripts were only tested on Cygwin - make sure it has correct line endings and fix it if not.