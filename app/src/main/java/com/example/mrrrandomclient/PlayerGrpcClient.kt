//package com.example.mrrrandomclient
//
//
//import com.example.mrrrandomclient.grpc.*
//import io.grpc.ManagedChannel
//import io.grpc.ManagedChannelBuilder
//
//
//
//class PlayerGrpcClient {
//    private val channel: ManagedChannel = ManagedChannelBuilder.forAddress("192.168.1.100", 9090)
//        .usePlaintext()
//        .build()
//
//    private val stub = PlayerServiceGrpc.newBlockingStub(channel)
//
//    fun registerPlayer(name: String): PlayerResponse {
//        val request = RegisterRequest.newBuilder().setName(name).build()
//        return stub.registerPlayer(request)
//    }
//
//    fun battle(): BattleResponse {
//        val request = BattleRequest.newBuilder().build()
//        return stub.battle(request)
//    }
//
//    fun getRanking(): RankingResponse {
//        val request = RankingRequest.newBuilder().build()
//        return stub.getRanking(request)
//    }
//
//    fun shutdown() {
//        channel.shutdown()
//    }
//}
