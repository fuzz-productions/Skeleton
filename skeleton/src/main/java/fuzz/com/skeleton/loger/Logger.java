/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */
package fuzz.com.skeleton.loger;

/**
 * Created by Ilya Gazman on 9/4/14.
 */
public class Logger {
    public static void log(Object...objects){
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : objects) {
            stringBuilder.append(object);
            stringBuilder.append(" ");
        }
        print(stringBuilder.toString());
    }

    private static void print(String message) {
        System.out.println("[Skeleton]:\t" + message);
    }
}
