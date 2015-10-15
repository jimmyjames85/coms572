# James Tappe coms572 

######################## Project 2 ############################################################

     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"
     THE WRITEUP FOR PROJECT 2 IS IN THE FILE "./report/report.pdf"

     The raw data is in "./report/rawdata" and the tables I created
     are in the .log files named with their strategy. E.g. ac34.log is
     the table for for the search strategy AC3 for 4-colorings.

######################## Project 2 ############################################################

To build and run type


	ant run2


This will compile a jar and echo the command to run it which is:

       java -jar build/jar/KColor.jar  color [STRATEGY] [GRAPH_FILE]

- or -

       java -jar build/jar/KColor.jar  create [FROM] [TO] [COPIES] [GRAPH_DIR]


KColor color arguments

       KColor color [STRATEGY] [GRAPH_FILE]

              color
			  reads in the graph file and attempts to find
                          both three and four k-coloring's using the
                          strategies outlined in this assignment. If
                          [STRATEGY] is not given it will run all
                          strategies. 


              [STRATEGY] 
                          Optional. Must be one of MIN_CONFLICTS,
                          BACKTRACKING, FC, AC3, MAC. Specifies the
                          backtracking and inference
                          preferences. 

              [GRAPH_FILE]
                          Graph file to color


KColor create arguments

       KColor create [FROM] [TO] [COPIES] [GRAPH_DIR]

              create   
			  will create graph files in GRAPH_DIR that
                          can be loaded for the color option. File
                          names have the format:

                              <node_total>_nodes_<copy_index>.txt

                          where each file has node_total nodes in it's
                          graph file and copy_index starts at 0 and
                          ends at [COPIES]-1. 


              [FROM]
                          node_total start index (inclusive, must be
                          greater than 2)

              [TO]
                          node_total end index (inclusive)

              [COPIES]
                          number of copies to create. 

              [GRAPH_DIR]
                          where to save or load graphs from





######################## Project 1 ############################################################

To build and run type 


	ant run


This will compile a jar and echo the command to run it which is:


	java -jar build/jar/UrlSearch.jar  [URL_START] [BREADTH | DEPTH | BEST | BEAM] {SEARCH_STRING} 



Task 2
a. Is my heuristic admissible?

   No because my heuristic was based on awarding negative points. Cost
   should is positive. I awarded points to single matches of keywords
   in the page containing the link in questions. I then awarded points
   to the keywords in the hypertext of the link. I weighted the link's
   keywords favoring consecutive in order matches the most. Without
   actually checking the link's page content there is no way guarantee
   the function will not be admissible.

c. How well did my heuristic do?

   My heuristic was comparable to the sample results given. 


intranet#   breadth  depth   sortByH  sortByH(Beam=10)
-------------------------------------------------------
    1        91/4    60/7     63/4    63/- 
    5        88/8    58/8     67/9    43/-
    7        55/6    58/19    28/10   25/10 


WWW Adventure

To run the WWW Adventure enter:

	java -jar build/jar/UrlSearch.jar "http://htmlparser.sourceforge.net/old/main.html"  BREADTH true "The filters package contains example"

This takes awhile and I found that sometimes the content of the page
isn't always available. Not because the site changes but I don't think
I retrieved the web contents correctly.
