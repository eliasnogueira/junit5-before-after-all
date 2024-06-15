/*
 * MIT License
 *
 * Copyright (c) 2024 Elias Nogueira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.eliasnogueira;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import static org.junit.jupiter.api.extension.ExtensionContext.Store;

public class SetupExtension implements BeforeAllCallback, Store.CloseableResource {

    private static final Lock LOCK = new ReentrantLock();
    private static volatile boolean started = false;

    /*
     * The LOCK prevents running multiple runs in the same thread
     * The started is used as an indicator for other threads
     * The context is set to be a flag for the ExtensionContext.Store when it closes
     * The close simulates the after all, as there is no way to control when the last test is executed using the AfterAllCallback
     *
     * Reference: https://stackoverflow.com/questions/43282798/in-junit-5-how-to-run-code-before-all-tests
     * Thanks to this user https://stackoverflow.com/users/325868/leo
     */
    @Override
    public void beforeAll(ExtensionContext context) {
        LOCK.lock();

        try {
            if (!started) {
                started = true;
                System.out.println("[pre-condition] Should run only once");
                context.getRoot().getStore(Namespace.GLOBAL).put("Placeholder for post condition", this);
            }
        } finally {
            // free the access
            LOCK.unlock();
        }
    }


    @Override
    public void close() {
        System.out.println("[post-condition] Should run only once");
    }
}
