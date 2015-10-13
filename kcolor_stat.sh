for n in {3..750}
do
    java -jar build/jar/KColor.jar color $n $n 10 graphs
done
