Home Address Miner Application

This test applicaiton tries to guess device home address based on the data provided in csv file.

Algorithm description.

2 assumptions were made based on manual analisys of the data for few devices:
1) Device owners do sleep. They typically do sleep at home. And device is not used during this time - meaning no data for the period. So, if we extract coordinates just before and right after the sleep period we may consider these coordinates to be a home address coordinate candidates.
2) Device owner spends some time at home (some time on weekdays and a lot of time on weekends) and device is located at nearly the same place during this period. Meaning that there will probably be a lot of points close to home coordinates overall.

Also looking at real data it turns out that day cycle is completely different for different device owners, so it's problemmatic to take into consideration device timezone.

Based on that the following algorithm was designed:
1) Filter empty lines and header from input data.
2) Aggregate data by device ID and sort by timestamp. This will allow to break work between the cluster machines.
3) Locate points which have meaningful (3-24 hours) period between them and cluster them. Get 5 clusters with highest weight. Cluster mass centers will be the candidates for home location. Clustering is limited to max distance.
4) Cluster all the points around these candidates. Clustering is limited to max distance. Get cluster with highest weight and consider it's mass center as home coordinates.

Running the applicaion (includes build, test and execute).

On both platforms scripts are assuming that Maven is installed and available in path. Also, "hdfs" command should be available in path.
HDFS version used by the application is 2.7.2. Should be compatible across major release though.

Windows:
1) Modify properties.bat file according to your environment.
2) Run run.bat script.

Linux:
1) Modify properties.sh file according to your environment.
2) Run run.sh script.

Results are retrieved from HDFS in ./results.csv file.