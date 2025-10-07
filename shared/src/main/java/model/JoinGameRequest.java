package model;

import chess.ChessGame;

public record JoinGameRequest(int gameID, ChessGame.TeamColor color, String authToken) {}
