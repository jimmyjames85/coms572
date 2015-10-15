for n in 800 900 1000 1100 1200 1300 1400 1500 1600 1700 1800 1900 2000
do
    for c in {0..9}
    do
#	for s in MIN_CONFLICTS BACKTRACKING FC AC3 MAC
	for s in MIN_CONFLICTS AC3
	do
	    java -jar build/jar/KColor.jar color ${s} ./bigGraphs/${n}_nodes_${c}.txt
	done
    done
done
