package rs.ac.bg.etf.running.firebase;

import com.google.firebase.auth.FirebaseAuth;

public abstract class FirebaseAuthInstance {

    private static FirebaseAuth instance = null;

    public static FirebaseAuth getInstance() {
        if(instance == null) {
            synchronized (FirebaseAuthInstance.class) {
                if(instance == null) {
                    instance = FirebaseAuth.getInstance();
                }
            }
        }

        return instance;
    }
}
