# Script to try to read chrome database file
# Found at C:\Users\Bai\AppData\Local\Google\Chrome\User Data\Default\History

import sqlite3
import datetime

def chrome_timestamp_to_unix(x):
  return int(x * 9.99992778555301 * 10**-7 - 11644379122)

def main():
  conn = sqlite3.connect('History.db')
  cur = conn.cursor()
  cur.execute('SELECT last_visit_time FROM urls')
  raw_list = cur.fetchall()

  ts_list = []
  for ts in raw_list:
    ts_list.append(ts[0])

  ts_list = sorted(ts_list)
  for ts in ts_list:
    unix_ts = chrome_timestamp_to_unix(ts)
    dt = datetime.datetime.fromtimestamp(unix_ts)
    print dt

# This tool is able to collect all timestamps from chrome history.
# However, it doesn't account for timezone changes (all timestamps are
# stored in UTC)
main()
