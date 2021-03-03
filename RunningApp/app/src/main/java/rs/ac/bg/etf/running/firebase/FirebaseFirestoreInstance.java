package rs.ac.bg.etf.running.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class FirebaseFirestoreInstance {

    private static FirebaseFirestore instance = null;

    public static FirebaseFirestore getInstance() {
        if(instance == null) {
            synchronized (FirebaseFirestoreInstance.class) {
                if(instance == null) {
                    instance = FirebaseFirestore.getInstance();
                }
            }
        }

        return instance;
    }
}
