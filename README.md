# Ateam53 Quiz Tool  
Marwan McBride - mkmcbride3@wisc.edu   
Junyu Wang        - junyu.wang@wisc.edu   
Christopher D’Amico - damico2@wisc.edu   
Declan Campbell - idcampbell@wisc.edu   

#### While displaying the image of question, if there is no image, the program display a 200x200 black image. If there is an image without valid path, the program will not display anything. 

### Instructions for adding a question:

#### NOTE . 

#1. If there is no image to display, please input 'none'

#2. Make sure you input four choices,\n one of which contains the right answer

#3. We assume that the user will put no more than 5 choices\n Fill unused answers with 'N/A' to denote that they are not choices.


### Command on the index page

#1 you can select a topic, you can see the label under the "Start Quiz". And you will see the total number of different questions in the upper right corner.  

#2 You can add a question. See NOTE above.

#3 You can import Questions to initilize the questions from a given file

#4 You can save the quiz(this won't close the window)

#5 You can exit without saving


### Process

#1. You will see a 200x200 image displayed every question. Upon, closing that and choosing an answer, you will see the accuracy of your choice.


#2. After answering all the questions, you will see your results, and you can go back to the home screen.

#3. Save, exit, or set the configuration and start the quiz again from the home screen



### File directory
.
├── README.md   
├── application  
│   ├── Main.class  
│   ├── Main.java   
│   ├── Question.class   
│   └── Question.java   
├── bin   
├── executable.jar   
├── goodhash2_AK.jpg   
├── json-simple-1.1.1.jar   
├── makefile   
├── manifest.txt   
└── validquestions.json   

```
git clone repo
```

run the program
```
make runjar
```

### Thank you!

