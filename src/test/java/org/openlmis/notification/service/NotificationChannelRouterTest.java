/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openlmis.notification.service.NotificationChannelRouter.EMAIL_SEND_NOW_CHANNEL;
import static org.openlmis.notification.service.NotificationChannelRouter.SMS_SEND_NOW_CHANNEL;

import org.junit.Test;

public class NotificationChannelRouterTest {

  private NotificationChannelRouter router = new NotificationChannelRouter();

  @Test
  public void routeShouldReturnEmailChannelForEmailNotificationChannel() {
    assertThat(router.route(NotificationChannel.EMAIL)).isEqualTo(EMAIL_SEND_NOW_CHANNEL);
  }
  
  @Test
  public void routeShouldReturnSmsChannelForSmsNotificationChannel() {
    assertThat(router.route(NotificationChannel.SMS)).isEqualTo(SMS_SEND_NOW_CHANNEL);
  }

  @Test
  public void routeShouldReturnNullForUnknownNotificationChannel() {
    assertThat(router.route(null)).isNull();
  }
}
