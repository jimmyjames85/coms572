# James Tappe coms572 


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
