package com.pv_libs.coroutine_cachepro.utils

class NoConnectionError : Exception("Not connected to any network")

class ConnectedButNoInternet : Exception("Connected but no internet access")