/*
 * Copyright (C) 2017 Mindgamesnl
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.openaudiomc.socket;

import net.openaudiomc.internal.events.SocketConnectEvent;
import net.openaudiomc.internal.events.SocketDisconnectEvent;
import net.openaudiomc.core.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class TimeoutManager implements Listener {
  public static Boolean ioconnected = false;
  public static Boolean ioready = false;
  public static Integer onlineplayers = 0;
  public static Boolean connecting = false;

  @EventHandler public void onSocketConnected(SocketConnectEvent event) {
    connecting = false;
    ioconnected = true;
    ioready = true;
    cm_callback.connections_made++;
  }

  public static void requestConnect() {
    try {
      if (!ioconnected) {
        cm_callback.update();
        Bukkit.getLogger().info("[OpenAudio] Reconnecting to the openaudiomc socket server.");
        if (!connecting) {
          connecting = true;
          SocketioConnector.connect();
        }
      }
    } catch (Exception e) {
      Bukkit.getLogger()
          .info(
              "[OpenAudio] Failed to connect to the socket.io server, openaudio will not work correctly.");
    }
  }

  public static void updateCounter() {
    onlineplayers = 0;
    for (Player p : Bukkit.getOnlinePlayers()) {
      onlineplayers++;
    }

    Integer connectedPlayersCount = 0;
    List<Boolean> list = new ArrayList<Boolean>(EventListener.isConnected.values());
    for (Boolean value : list) {
      if (value) {
        connectedPlayersCount++;
      }
    }

    if (connectedPlayersCount == 0) {
      if (ioconnected) {
        cm_callback.update();
        Bukkit.getLogger().info("[OpenAudio] Closing connection with the socket server.");
        SocketioConnector.close();
      }
      return;
    }
    if (onlineplayers == 0) {
      if (ioconnected) {
        cm_callback.update();
        Bukkit.getLogger().info("[OpenAudio] Closing connection with the socket server.");
        SocketioConnector.close();
      } else {
        Bukkit.getLogger()
            .info(
                "[OpenAudio] Connection with socket server is already closed, skipping closing thingy.");
      }
    } else {
      /*try {
				if (!ioconnected) {
					cm_callback.update();
					Bukkit.getLogger().info("[OpenAudio] Reconnecting to the openaudiomc socket server.");
					if (!connecting) {
						connecting = true;
						SocketioConnector.connect();
					}
				}
			} catch (Exception e) {
				Bukkit.getLogger().info("[OpenAudio] Failed to connect to the socket.io server, openaudio will not work correctly.");
			}*/
    }
  }

  @EventHandler public void onSocketDisconnected(SocketDisconnectEvent event) {
    ioconnected = false;
    cm_callback.connections_closed++;
  }
}