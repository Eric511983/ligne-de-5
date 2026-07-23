package com.eric511983.lignede5;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayGames;

@CapacitorPlugin(name = "PlayGames")
public class PlayGamesPlugin extends Plugin {

    @PluginMethod
    public void signIn(PluginCall call) {
        GamesSignInClient signInClient = PlayGames.getGamesSignInClient(getActivity());
        signInClient
            .isAuthenticated()
            .addOnCompleteListener(isAuthenticatedTask -> {
                boolean authenticated = isAuthenticatedTask.isSuccessful() && isAuthenticatedTask.getResult().isAuthenticated();
                if (authenticated) {
                    JSObject ret = new JSObject();
                    ret.put("signedIn", true);
                    call.resolve(ret);
                } else {
                    signInClient
                        .signIn()
                        .addOnCompleteListener(signInTask -> {
                            boolean success = signInTask.isSuccessful() && signInTask.getResult().isAuthenticated();
                            JSObject ret = new JSObject();
                            ret.put("signedIn", success);
                            call.resolve(ret);
                        });
                }
            });
    }

    @PluginMethod
    public void submitScore(PluginCall call) {
        String leaderboardId = call.getString("leaderboardId");
        Integer score = call.getInt("score");
        if (leaderboardId == null || score == null) {
            call.reject("leaderboardId et score requis");
            return;
        }
        LeaderboardsClient client = PlayGames.getLeaderboardsClient(getActivity());
        client.submitScore(leaderboardId, score);
        call.resolve();
    }

    @PluginMethod
    public void showLeaderboard(PluginCall call) {
        String leaderboardId = call.getString("leaderboardId");
        if (leaderboardId == null) {
            call.reject("leaderboardId requis");
            return;
        }
        LeaderboardsClient client = PlayGames.getLeaderboardsClient(getActivity());
        client
            .getLeaderboardIntent(leaderboardId)
            .addOnSuccessListener(intent -> {
                getActivity().startActivityForResult(intent, 9004);
                call.resolve();
            })
            .addOnFailureListener(e -> call.reject("Impossible d'ouvrir le classement", e));
    }
}
