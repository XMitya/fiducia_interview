package ru.rustore;

public class CountParallelProcesses {
   /*
there are logs from a service, and for each request we have a record like:
getData <unix start time> <unix end time>
find maximum number of parallel requests.

getData 1742129969123 1742139969123
getData 1742229970122 1742239969122
getData 1742129971123 1742229972122


               -----
       ----------  -------         --
      ----    -------    ------   -----
   ------------------------ ---------------



                  -----
       ----------  -------         --
      ----    -------    ------   -----
   ------------------------ ---------------

-----------------------------t------------------------->

---1--11-------------------------------------------------
---------1----------1-----1------------------------------
*/
}
