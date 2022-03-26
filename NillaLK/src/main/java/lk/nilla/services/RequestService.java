package lk.nilla.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import lk.nilla.contentManagement.Request;
import lk.nilla.contentManagement.RequestChangeCont;
import lk.nilla.contentManagement.RequestRegShop;
import lk.nilla.contentManagement.RequestState;
import lk.nilla.contentManagement.RequestPrioritize;
import lk.nilla.contentManagement.RequestNewProduct;
import lk.nilla.contentManagement.RequestType;

public class RequestService {
	
	private DBConnection connection = new DBConnection();

	/** Get request by requestID
	 * @param requestID
	 * @return Request object containing data
	 */
	public Request getRequest(int requestID) throws Exception{

		Request request;
		try {
			ResultSet rs = new SQLStatement("SELECT", "Request").where("requestID", requestID).generate(connection).executeQuery();

			if(!rs.next()) {
				return null;
			}
			request = new Request(rs.getInt(1), rs.getDate(2), RequestState.valueOf(rs.getString(3)), rs.getInt(4), RequestType.valueOf(rs.getString(5)));
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// Get Details in RequestType table
		try {
//			PreparedStatement statementGetRequestType = connection.prepare("SELECT * FROM " + request.requestType + " WHERE RequestID = ?");
//			statementGetRequestType.setInt(1, requestID);
//			ResultSet rs = statementGetRequestType.executeQuery();

			ResultSet rs = new SQLStatement("SELECT", "Request"+request.requestType.toString()).where("requestID", requestID).generate(connection).executeQuery();
					
			if(!rs.next()) {
				return null;
			}
			
			if(request.requestType == RequestType.NewProduct) {
				RequestNewProduct temp = new RequestNewProduct();
				temp.requestID = request.requestID;
				temp.requestDate = request.requestDate;
				temp.requestState = request.requestState;
				temp.requestType = request.requestType;
				temp.newProductType = rs.getString(2);
				temp.newProductName = rs.getString(3);
				temp.newProductDes = rs.getString(4);
				temp.newProductImg = rs.getString(5);
				request = temp;
			} else if(request.requestType == RequestType.RegShop) {
				RequestRegShop temp = new RequestRegShop();
				temp.requestID = request.requestID;
				temp.requestDate = request.requestDate;
				temp.requestState = request.requestState;
				temp.requestType = request.requestType;
				temp.regDesc = rs.getString(2);
				request = temp;
			} else if(request.requestType == RequestType.Prioritize) {
				RequestPrioritize temp = new RequestPrioritize();
				temp.requestID = request.requestID;
				temp.requestDate = request.requestDate;
				temp.requestState = request.requestState;
				temp.paymentImage = rs.getString(2);
				request = temp;
			} else if(request.requestType == RequestType.ChangeCont) {
				RequestChangeCont temp = new RequestChangeCont();
				temp.requestID = request.requestID;
				temp.requestDate = request.requestDate;
				temp.requestState = request.requestState;
				temp.changeType = rs.getString(2);
				temp.changeTitle = rs.getString(3);
				temp.changeDesc = rs.getString(4);
				request = temp;
			} else {
				throw new Exception("Invalid Request Type");
			} 
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return request;
	}
	
	/** Get request with following filtering methods.
	 * @param userID - provide 0 to skip filtering using userID
	 * @param requestType - provide null to skip filtering using requestType
	 * @param requestState - provide null to skip filtering using requestState
	 * @param limit - provide 0 to skip limiting number of articles returned
	 * @param offset - provide 0 to skip offset. Ignored if limit is already 0
	 * @return ArrayList{@literal <}Article{@literal >} - array list of Article. Empty ArrayList (size == 0) if no results
	 */
	public ArrayList<Request> getRequests(int userID, String requestType, String requestState, int limit, int offset) throws Exception{
		ArrayList<Request> requests = new ArrayList<Request>(0);
			
		try {
			SQLStatement stmt = new SQLStatement("SELECT", "Request");

			if(userID > 0) {
				stmt = stmt.where("userID", userID);				
			}
			if(requestType != null) {
				stmt = stmt.where("requestType", requestType);				
			}
			if(requestState != null) {
				stmt = stmt.where("requestState", requestState);				
			}
			if(limit > 0) {
				stmt = stmt.limit(limit).offset(offset);				
			}
			System.out.println(stmt);
			ResultSet rs = stmt.generate(connection).executeQuery();
			
			while(rs.next()) {
				requests.add(new Request(rs.getInt(1), rs.getDate(2), RequestState.valueOf(rs.getString(3)), rs.getInt(4), RequestType.valueOf(rs.getString(5))));
			}
			return requests;
				
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** creates a new request
	 * @param request - Request object
	 * @return newly created requestID
	 */
	public int addRequest(Request request) throws Exception{
		int requestIndex = 0;
		
		try {
			requestIndex = createRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			createRequestType(request, requestIndex);
		} catch (Exception e) {
			try {
				deleteRequest(requestIndex, request.userID);
			} catch (Exception e2) {
				e2.printStackTrace();
				return -1;
			}
		}		
		
		return requestIndex;
	}
	
	public int createRequest(Request request) throws Exception{
			
		try {
			//create request in Request table
			PreparedStatement stmt = new SQLStatement("INSERT", "Request")
					.set("requestDate", request.requestDate)
					.set("requestState", request.requestState.toString())
					.set("userID", request.userID)
					.set("requestType", request.requestType.toString())
					.generate(connection);
			stmt.executeUpdate();
				
			ResultSet rs = stmt.getGeneratedKeys();
			int requestIndex = 0;
			if (rs.next()) {
				requestIndex = rs.getInt(1);				
			}
			rs.close();
			return requestIndex;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	// Insert data into RequestType table for other details
	public int createRequestType(Request request, int requestIndex) throws Exception{
		try {
			PreparedStatement stmt;
			if(request.requestType == RequestType.NewProduct) {
				stmt = new SQLStatement("INSERT", "RequestNewProduct")
						.set("requestID", requestIndex)
						.set("newProductType", ((RequestNewProduct)request).newProductType)
						.set("newProductName", ((RequestNewProduct)request).newProductName)
						.set("newProductDes", ((RequestNewProduct)request).newProductDes)
						.set("newProductImg", ((RequestNewProduct)request).newProductImg)
						.set("shopID", ((RequestNewProduct)request).shopID)
						.generate(connection);
			} else if(request.requestType == RequestType.RegShop) {
				stmt = new SQLStatement("INSERT", "RequestRegShop")
						.set("requestID", requestIndex)
						.set("regDesc", ((RequestRegShop)request).regDesc)
						.set("shopID", ((RequestRegShop)request).shopID)
						.generate(connection);
			} else if(request.requestType == RequestType.Prioritize) {
				stmt = new SQLStatement("INSERT", "RequestPrioritize")
						.set("requestID", requestIndex)
						.set("paymentImage", ((RequestPrioritize)request).paymentImage)
						.set("shopID", ((RequestPrioritize)request).shopID)
						.generate(connection);
			} else if(request.requestType == RequestType.ChangeCont) {
				stmt = new SQLStatement("INSERT", "RequestChangeCont")
						.set("requestID", requestIndex)
						.set("changeType", ((RequestChangeCont)request).changeType)
						.set("changeTitle", ((RequestChangeCont)request).changeTitle)
						.set("changeDesc", ((RequestChangeCont)request).changeDesc)
						.generate(connection);
			} else {
				throw new Exception("Unknown Request Type");
			}
			stmt.executeUpdate();
			return requestIndex;
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}		
	}
	
	/** Update request details.
	 * @param request - Request object with updates
	 * @param userID - requesting user's userID. Limits updating only if the request belongs to the given user. Provide 0 if user is an admin to skip checking against userID
	 * @return number of updated rows
	 */

	public int changeRequest(Request request, int userID) throws Exception {
		
		try {
			updateRequest(request, userID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			updateRequestType(request, userID);
		} catch (Exception e) {
			try {
				deleteRequest(request.requestID, request.userID);
			} catch (Exception e2) {
				e2.printStackTrace();
				return -1;
			}
		}		
		
		return 1;
	}	
	
	public int updateRequest(Request request, int userID) throws Exception{
		try {
			SQLStatement stmt = new SQLStatement("UPDATE", "Request");
			if(request.requestState != null) {
				stmt.set("requestState", request.requestState);
			}
			stmt = stmt.where("requestID", request.requestID);
			
			if(userID > 0) {
				stmt = stmt.where("userID", userID);				
			}

			PreparedStatement prepstmt = stmt.generate(connection); // generate prepared statement (assign so it can be used later)
			prepstmt.executeUpdate();
			// get number of affected rows so we can decide whether the update was successful or not (either 0 or 1)
			int rows = prepstmt.getUpdateCount();
			return rows; // 0 if failed
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	// update request types table only
	public int updateRequestType(Request request, int userID) throws Exception{
		try {
			SQLStatement stmt;
			if(request.requestType == RequestType.NewProduct) {
				stmt = new SQLStatement("UPDATE", "RequestNewProduct");
				if(((RequestNewProduct)request).newProductType != null) {
					stmt.set("newProductType", ((RequestNewProduct)request).newProductType);
				}
				if(((RequestNewProduct)request).newProductName != null) {
					stmt.set("newProductName", ((RequestNewProduct)request).newProductName);
				}
				if(((RequestNewProduct)request).newProductDes != null) {
					stmt.set("newProductDes", ((RequestNewProduct)request).newProductDes);
				}
			} else if(request.requestType == RequestType.RegShop){
				stmt = new SQLStatement("UPDATE", "RequestRegShop");
				if(((RequestRegShop)request).regDesc != null) {
					stmt.set("regDesc", ((RequestRegShop)request).regDesc);
				}				
			} else if(request.requestType == RequestType.Prioritize){
				stmt = new SQLStatement("UPDATE", "RequestPrioritize");
				if(((RequestPrioritize)request).paymentImage != null) {
					stmt.set("regDesc", ((RequestPrioritize)request).paymentImage);
				}				
			} else if(request.requestType == RequestType.ChangeCont){
				stmt = new SQLStatement("UPDATE", "RequestChangeCont");
				if(((RequestChangeCont)request).changeType != null) {
					stmt.set("changeType", ((RequestChangeCont)request).changeType);
				}
				if(((RequestChangeCont)request).changeTitle != null) {
					stmt.set("changeTitle", ((RequestChangeCont)request).changeTitle);
				}				
				if(((RequestChangeCont)request).changeDesc != null) {
					stmt.set("changeDesc", ((RequestChangeCont)request).changeDesc);
				}				
			} else {
				throw new Exception("Unknown Request Type");
			}

			stmt = stmt.where("requestID", request.requestID);

			if(userID > 0) {
				stmt = stmt.where("userID", userID);				
			}
			
			PreparedStatement prepstmt = stmt.generate(connection); // generate prepared statement (assign so it can be used later)
			prepstmt.executeUpdate();
			// get number of affected rows so we can decide whether the update was successful or not (either 0 or 1)
			int rows = prepstmt.getUpdateCount();
			return rows; // 0 if failed
			
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	// delete details in request table and request type table
	public int deleteRequest(int requestID, int userID) throws Exception{
		try {
			SQLStatement stmt = new SQLStatement("DELETE", "Request").where("requestID", requestID);
			if(userID > 0) {
				stmt = stmt.where("userID", userID);				
			}
			PreparedStatement prepstmt = stmt.generate(connection); // generate prepared statement (assign so it can be used later)
			prepstmt.executeUpdate();
			// get number of affected rows so we can decide whether the update was successful or not (either 0 or 1)
			int rows = prepstmt.getUpdateCount();
			return rows; // 0 if failed			
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	void close() {
		connection.close();
	}
	
}
	
	