/**
 *      Created by Pasquale Trinchese
 *      pasquale.trinchese@cern.ch
 */

package com.bci_ar.ar_bci_rasp_arrow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {

    // SQUARE
    private float dimx = 1.0f;
    private float dimy = 1.0f;
    private float vertices[] = {
            -dimx,  dimy, 0.0f,  // 0, Top Left
            -dimx, -dimy, 0.0f,  // 1, Bottom Left
             dimx, -dimy, 0.0f,  // 2, Bottom Right
             dimx,  dimy, 0.0f,  // 3, Top Right
    };

    private short indices[] = { 0, 1, 2, 0, 2, 3 };

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    public Square() {

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }

    //DRAW SQUARE TO SCREEN
    public void draw(GL10 gl, float [] color) {
        gl.glColor4f(color[0], color[1], color[2], 1.0f);

        gl.glFrontFace(GL10.GL_CW); //XXXXX  GL10.GL_CCW
       // gl.glEnable(GL10.GL_CULL_FACE);
       // gl.glCullFace(GL10.GL_BACK);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0,vertexBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,GL10.GL_UNSIGNED_SHORT, indexBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
       // gl.glDisable(GL10.GL_CULL_FACE);
    }

}