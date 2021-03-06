/*
 * Copyright 2020 DarksideCode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.darksidecode.keiko.runtimeprotect;

import lombok.Getter;
import me.darksidecode.keiko.KeikoPluginInspector;
import me.darksidecode.keiko.Platform;
import me.darksidecode.keiko.runtimeprotect.dac.KeikoSecurityManager;

public class RuntimeProtect {

    private static final String HELP_URL = "https://github.com/MeGysssTaa/keiko-plugin-inspector" +
            "/wiki/Common-Issues#2jvm-security-manager-is-already-set-error-on-bungeecord";

    @Getter
    private boolean enabled;

    public void setupDomainAccessControl() {
        KeikoPluginInspector.debug("Setting up Keiko security manager");

        SecurityManager curSec    = System.getSecurityManager();
        String curSecurityManager = System.getProperty("java.security.manager");
        String curSecurityPolicy  = System.getProperty("java.security.policy");

        if ((curSec != null) || (curSecurityManager != null) || (curSecurityPolicy != null))
            KeikoPluginInspector.warn("JVM security manager is already set (%s). " +
                    "Domain access control may not work properly. Check your start command arguments for: " +
                    "'java.security.manager' (%s), 'java.security.policy' (%s).",
                    (curSec == null) ? "N/A" : curSec.getClass().getName(), curSecurityManager, curSecurityPolicy);

        try {
            SecurityManager mgr = new KeikoSecurityManager();
            System.setSecurityManager(mgr);

            enabled = true; // indicate that Keiko DAC was successfully enabled
        } catch (SecurityException ex) {
            KeikoPluginInspector.warn("Failed to setup Keiko Domain Access Control.");
            KeikoPluginInspector.warn("Keiko will be unable to protect your server at runtime.");

            if (KeikoPluginInspector.getPlatform() == Platform.BUNGEECORD) {
                KeikoPluginInspector.warn("Visit Keiko wiki for help:");
                KeikoPluginInspector.warn("> %s", HELP_URL);
            } else {
                KeikoPluginInspector.warn("This kind of issue is unusual for Bukkit/Spigot servers.");
                KeikoPluginInspector.warn("Please report this on Github or Discord!");
            }

            ex.printStackTrace();
        }
    }

}
