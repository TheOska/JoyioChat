# JoyioChat
### Final Year Project - video based communication application (SnapChat Like AR 3D model map to user face)
![alt text](https://firebasestorage.googleapis.com/v0/b/joyiochat.appspot.com/o/18512418_673524546168109_1936180796159164416_n.jpg?alt=media&token=9df5d66e-5f6c-401f-aae2-d9be00d39b81)
### Demonstration/Promotional  video 
https://youtu.be/0_DKc9hetVM
### Theory
![alt_text](https://firebasestorage.googleapis.com/v0/b/joyiochat.appspot.com/o/Theory.png?alt=media&token=a8eed1dc-4e8b-4699-a53d-4c9972dc5157)
(a)First and foremost, users need to make the corresponding emotion or head motion, such that the application can detect those action.

(b)To implement emotion detect and head motion detection, Google Mobile Vision API will be used in this application, the reason is it is free, it also hide some of the difficulty to detect facial emotion. Through this API, some landmarks will be detected. We can take those landmark to add the effects.

(c),(d)  After detect the user emotion, we then started to analysis that which kinds of
 emotion that user is performing. Next we extract the analysis result to match
 the predefined set of emotions condition.

(e,f,h)  Next, after matching the condition, it will extract the preset 3D models(include
 2D, 3D graphics and objects). To implement the rendering part, OpenGL and
 RAJAWALI will be used in this application. The usage of OpenGL is a library
 to rendering 2D and 3D vector graphics. Via OpenGL, we can interact will
 graphics processing unit to achieve hardware accelerated rendering. Mean-
 while, RAJAWALI is a framework for android application, it can easily to
 interact between Android and OpenGL, make it more lite to render the 3D
 models in Android.
 
~~Finally, after the 3D models are rendered, we will use the Vuforia library, Vuforia is an Augmented Reality Software Development Kit for mobile devices, it enables the creation of Augmented Reality applications. Via this SDK, we map the corresponding 3D models to the user face~~

(For the latest updated version, we finally dosen’t need Vuforia for AR 3d project, because what we have been done, is to map the 3D models using RAJAWALI into detected user face. Such that we doesn’t need this library support )
