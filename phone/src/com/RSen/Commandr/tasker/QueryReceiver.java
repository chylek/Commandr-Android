/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.RSen.Commandr.tasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.RSen.Commandr.util.ActivationCheck;

import java.util.ArrayList;
import java.util.regex.MatchResult;

/**
 * This is the "query" BroadcastReceiver for a Locale Plug-in condition.
 */
public final class QueryReceiver extends BroadcastReceiver {

    /**
     * @param context {@inheritDoc}.
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        /*
         * Always be strict on input parameters! A malicious third-party app could send a malformed Intent.
         */

        if (!LocaleIntent.ACTION_QUERY_CONDITION.equals(intent.getAction())) {
            return;
        }

        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(LocaleIntent.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);

        if (PluginBundleManager.isBundleValid(bundle)) {
            try {
                final String lastPhrase = TaskerPlugin.Event.retrievePassThroughData(intent).getString("interceptedCommand");
                final String searchPhrase = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_PHRASE);
                final boolean isRegex =  bundle.getBoolean(PluginBundleManager.BUNDLE_EXTRA_REGEX, false);

                if (TaskerPlugin.Event.retrievePassThroughMessageID(intent) == -1)
                    setResultCode(LocaleIntent.RESULT_CONDITION_UNKNOWN);
                else {

                    boolean commandFound = false;
                    String phraseResult = "";
                    ArrayList<String> regexResult = null;
                    if (!isRegex) {
                        phraseResult = ActivationCheck.phraseActivation(lastPhrase, searchPhrase.trim().toLowerCase());
                        if (phraseResult != null){
                            commandFound = true;
                        }
                    } else {
                        MatchResult mr = ActivationCheck.regexActivation(lastPhrase,searchPhrase.trim());
                        if (mr != null){
                            commandFound = true;
                            regexResult = new ArrayList<>();
                            for (int i=0;i<=mr.groupCount();i++){
                                regexResult.add(mr.group(i));
                            }
                        }
                    }
                    if (commandFound) {
                        if ( TaskerPlugin.Condition.hostSupportsVariableReturn( intent.getExtras() ) ) {
                            Bundle varsBundle = new Bundle();
                            varsBundle.putString("%commandr_text", lastPhrase.trim());
                            if (regexResult!=null){
                                for (int j=0;j<regexResult.size();j++){
                                    varsBundle.putString("%commandr_"+j,regexResult.get(j));
                                }
                            }
                            TaskerPlugin.addVariableBundle( getResultExtras( true ), varsBundle );
                        }
                        setResultCode(LocaleIntent.RESULT_CONDITION_SATISFIED);
                    } else {
                        setResultCode(LocaleIntent.RESULT_CONDITION_UNSATISFIED);
                    }
                }
            } catch (Exception e) {
                setResultCode(LocaleIntent.RESULT_CONDITION_UNSATISFIED);
            }

        }
    }
}