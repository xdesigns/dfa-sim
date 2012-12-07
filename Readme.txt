Deterministic FInite Automaton Simulator
By Nijith Jacob (nijith89@gmail.com)

A Deterministic finite automaton is a finite state machine defined by 
   K, the number of states,   
   Σ, an alphabet set, 
   sεK, an initial state,    
   F, a subset ok K number of final states, and
   δ, a transition function

The program functions in 3 Steps
Step 1: Specifying a DFA using inbuilt creator or from an external file (.dfa).
        1. In-built creator creates the DFA in 5 modes namely,
	    Selection : Used to specify the number of nodes. The nodes can be activated by clicking on any Inactive nodes.        
	    Set Zero : Used to specify the transition function associated with the first symbol ('0')        
	    Set One : Used to specify the transition function associated with the second symbol ('1')        
	    Start Node : Used to specify the starting node. Any active or final node can be specified as the starting node. Only one starting node can be specified.         
	    Final Nodes : To set the final nodes. Any active or starting node can be specified as final.
	   
        2. To select an external file, click on 'Load' button in the right panel. Only files saved using the program can be loaded.

Step 2: Feeding the String - The input string can be feeded directly through the 'Text field' in the bottom or from an external .txt file. 
        Prev and Next Buttons can be used to skip to the previous and next lines respectively.
Step 3: Running - The Start, Stop and Pause buttons can be used for their natural operation. A slider is also given to adjust the speed.
        The delay is inversely propotional to the input string length.
