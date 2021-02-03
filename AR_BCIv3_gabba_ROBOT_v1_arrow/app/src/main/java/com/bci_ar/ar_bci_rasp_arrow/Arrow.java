package com.bci_ar.ar_bci_rasp_arrow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Arrow {


    // ARROW
    private float vertices[] = {
            -1.0f, 0.0f, 0.0f,  // 0,
            0.0f, -1.5f, 0.0f, // 1,
            0.0f, 1.5f, 0.0f,  // 2,
            0.0f, 1.0f, 0.0f,  // 3
            0.0f, -1.0f, 0.0f,  // 4
            1.5f, -1.0f, 0.0f,  // 5
            1.5f, 1.0f, 0.0f,  // 6


    };

    private short indices[] = { 0, 1, 2, 3 , 4 , 5, 3, 5, 6};

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;

    public Arrow() {

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