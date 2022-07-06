package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component("ActiveTransferDao")
public class JdbcTransferDao implements TransferDao {
    private JdbcTemplate jdbcTemplate;
    public JdbcTransferDao(DataSource datasource){jdbcTemplate = new JdbcTemplate(datasource);}

    @Override
    public List<Transfer> getTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount FROM transfer t JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id ORDER BY transfer_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        while(sqlRowSet.next()){
            transfers.add(mapRowtoTransfer(sqlRowSet));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByTransferType(String transferType) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount FROM transfer t JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id WHERE transfer_type_desc = ? ORDER BY transfer_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql,transferType);
        while(sqlRowSet.next()){
            transfers.add(mapRowtoTransfer(sqlRowSet));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByTransferStatus(String transferStatus) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount FROM transfer t JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id WHERE transfer_status_desc = ? ORDER BY transfer_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql,transferStatus);
        while(sqlRowSet.next()){
            transfers.add(mapRowtoTransfer(sqlRowSet));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByOutgoingAccount(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount FROM transfer t JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id WHERE account_from = ? ORDER BY transfer_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql,accountId);
        while(sqlRowSet.next()){
            transfers.add(mapRowtoTransfer(sqlRowSet));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByIncomingAccount(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount FROM transfer t JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id WHERE account_to = ? ORDER BY transfer_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql,accountId);
        while(sqlRowSet.next()){
            transfers.add(mapRowtoTransfer(sqlRowSet));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransferByAccountNumber(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        transfers.addAll(getTransfersByOutgoingAccount(accountId));
        transfers.addAll(getTransfersByIncomingAccount(accountId));
        return transfers;
    }


    @Override
    public Transfer getTransfer(int id) {
        Transfer transfer = null;
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount FROM transfer t JOIN transfer_type AS tt ON t.transfer_type_id = tt.transfer_type_id JOIN transfer_status AS ts ON t.transfer_status_id = ts.transfer_status_id WHERE account_to = ? ORDER BY transfer_id";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        if(sqlRowSet.next()) {
            transfer = mapRowtoTransfer(sqlRowSet);
        }
        return transfer;
    }

    @Override
    public Transfer addTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES ((SELECT transfer_type_id FROM transfer_type WHERE transfer_type_desc = ?), (SELECT transfer_status_id FROM transfer_status WHERE transfer_status_desc = ?), ?,?,?) RETURNING transfer_id);";
        Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferType(), transfer.getTransferStatus(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmountToTransfer());
        return getTransfer(transferId);
    }

    @Override
    public void updateTransfer(Transfer transfer) {
        String sql = "UPDATE transfers SET transfer_type_id = (SELECT transfer_type_id FROM transfer_type WHERE transfer_type_desc = ?), transfer_status_id = (SELECT transfer_status_id FROM transfer_status WHERE transfer_status_desc = ?), account_from = ?, account_to = ?, amount = ? WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, transfer.getTransferType(), transfer.getTransferStatus(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmountToTransfer(), transfer.getId());
    }

    @Override
    public void deleteTransfer(int id) {
        String sql = "DELETE FROM transfers WHERE transfer_id = ?;";
        jdbcTemplate.update(sql, id);
    }

    private Transfer mapRowtoTransfer(SqlRowSet sqlRowSet){
        Transfer transfer = new Transfer();
        transfer.setId(sqlRowSet.getInt("transfer_id"));
        transfer.setTransferType(sqlRowSet.getString("transfer_type_desc"));
        transfer.setTransferStatus(sqlRowSet.getString("transfer_status_desc"));
        transfer.setAccountFromId(sqlRowSet.getInt("account_from"));
        transfer.setAccountToId(sqlRowSet.getInt("account_to"));
        transfer.setAmountToTransfer(sqlRowSet.getBigDecimal("amount"));

        return transfer;
    }
}
