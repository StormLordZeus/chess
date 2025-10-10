package model;

import chess.ChessGame;

public record JoinGameRequest(int gameID, String playerColor, String authToken) {}
