#
#
#
package BaseConfig;
use File::Basename;
use Cwd 'abs_path';

require Exporter;
@ISA = qw(Exporter);
@EXPORT = qw($root_dir
	     $verbose
	     $log_path
	     $pub_path
	     $csv_path
	     $bin_path
	     $csv_header
	     $processed_log_suffix
	     $ant
	     $max_sanity_log_bytes
	     $begining_of_time_hrs
	     $upload_target);

($b,$path,$s) = fileparse($0);
$root_dir = abs_path("$path../");

$begining_of_time_hrs = (int 1217240423/ (60 * 60));

$log_path = "log";
$pub_path = "pub";
$csv_path = "csv";
$bin_path = "bin";
$processed_log_suffix = ".plog";
$max_sanity_log_bytes = 10 * 1024;
$verbose = 1;
#my $regression_period_hrs = 3;
#$regression_period_sec = $regression_period_hrs * 60 * 60;

my $java_home = "/usr/lib/jvm/java-1.5.0-sun";
my $ant_opts = "-Xmx512M -Dhttp.proxyHost=150.203.163.152 -Dhttp.proxyPort=3128";
# -Dhttp.proxyHost=ra0.anu.edu.au -Dhttp.proxyPort=3128";
$ant = "export ANT_OPTS=\"$ant_opts\" && export JAVA_HOME=$java_home && ant ";

$csv_header = "\"#\n# comma-separated values\n# <id>, <hr number>, <elapsed sec for job>, <time itr 0>, ... , <time itr N-1>\n#\"";

$upload_target = "dacapo\@dacapo.anu.edu.au";
