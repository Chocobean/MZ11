#include <jni.h>
//Sobel Algorithm

JNIEXPORT void JNICALL Java_com_univie_mz11_OverlayView_doNativeProcessing(
	JNIEnv *jnienv, jobject this,
	jbyteArray frame, jint width, jint height, jobject diff)
{
	jboolean framecopy;
	//Pointer to the outputbuffer
	jint *databuffer = (jint*)((*jnienv)->GetDirectBufferAddress(jnienv, diff));
	//Pointer to the input
	jbyte *framebuffer = (*jnienv)->GetByteArrayElements(jnienv, frame, &framecopy);
	int x, y, maxx=width-1, maxy=height-1, p=width+1, px, py, ps;

	for(y=1; y<maxy; y++, p+=2)
	{
		for(x=1; x<maxx; x++, p++)
		{
			px = framebuffer[p+width+1]-framebuffer[p+width-1]
				 + framebuffer[p+1]+framebuffer[p+1]
				 - framebuffer[p-1]-framebuffer[p-1]
				 + framebuffer[p-width+1]-framebuffer[p-width-1];
			py = framebuffer[p-width-1]+framebuffer[p-width]
				 + framebuffer[p-width]+framebuffer[p-width+1]
				 - framebuffer[p+width-1]-framebuffer[p+width]
				 - framebuffer[p+width]-framebuffer[p+width+1];
			if(px<0) px=-px; if(py<0) py=-py;
			ps=px+py; if(ps>95) ps=255; if(ps<=95) ps=0;
			databuffer[p] = (ps<<24)|(ps<<16)|(ps<<8)|ps;
		}
	}
}
