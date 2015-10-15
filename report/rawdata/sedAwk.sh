if [ $# -lt 5 ]
then
    echo "args: strategy kcount nodecount_low nodecount_high [completion=100] logfile"
    exit -1;
fi
STRAT=$1
KCOUNT=$2
NCOUNTL=$3
NCOUNTH=$4
COMPLETION=100
LOGFILE=$5
if [ $# -eq 6 ]
then
    COMPLETION=$5;
    LOGFILE=$6
fi

if [ $NCOUNTH -lt $NCOUNTL ]
then
echo nodecount_low is not less than or equal to nodecount_high
exit
fi

NCOUNT=$NCOUNTL
NCOUNTH=$[$NCOUNTH+1]

while [ $NCOUNT -lt $NCOUNTH ]
do
    grep "<${NCOUNT}, ${KCOUNT},.*${STRAT}" ${LOGFILE} | \
	sed 's/\[.*\].*<//' | \
	sed 's/[\,%>]//g' | \
	awk -v completion=$COMPLETION -v ncount=$NCOUNT -v kcount=$KCOUNT -v strat=$STRAT 'BEGIN { time=0; count=0; }\
     {\

	if(completion)\
	{\
		if($3>=completion)\
		{\
			count++; time+=$5;	\
#			print $2,"color" ,$3, "\t", $5, "ms";\
		}\
	}\
	else\
	{\
		count++; time+=$5;	\
#		print $2,"color" ,$3, "\t", $5, "ms";\
	}\

     }\

     END{ if(count>0) {\
		print ncount,"\tnodes k=", kcount,strat, count, "reading(s) [assignment>=",completion ,"%] avg time =", time/count, "ms";\
	   }\
	}'
NCOUNT=$[$NCOUNT+1]
done
