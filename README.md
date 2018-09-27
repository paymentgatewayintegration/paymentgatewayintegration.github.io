# Overview

> This section will guide you in creating a framework for integrating the Payment Gateway with your android app. 

![Overview](https://paymentgatewayintegration.github.io/doc/images/overview.png?raw=true)

-------------

# Sample App
> To understand the Payment Gateway payment flow, you can download our sample app [here](https://github.com/paymentgatewayintegration/paymentgatewayintegration.github.io).

-------------

# Prerequisites

1. You should be a registered and approved merchant with Payment Gateway.
2. You should have received the SALT and API key from Payment Gateway.
3. You should have received the HOSTNAME for the Payment Gateway request URL.

-------------

# Server Side Setup

> a. To prevent the data tampering(and ensure data integrity) between the your app and Payment Gateway, you will need to setup up an API in your server to calculate an encrypted value or checksum known as hash from the payment request parameters and SALT key before sending it to the Payment Gateway server.

```markdown
Payment Gateway uses **SHA512** cryptographic hash function to prevent data tampering. To calculate the 
hash, a secure private key known as **SALT key** will be provided by Payment Gateway that needs to be 
stored **very securely in your server**. Any compromise of the salt may lead to data tampering. 

# The hash generation code has 3 components:

1. **Concatenate** the request parameters(after **trimming** the blank spaces) separated by 
**pipeline** in the order given below:   

`hash_data="SALT|address_line_1|address_line_2|amount|api_key|city|country|currency|description
|email|hash|mode|name|order_id|phone|return_url|state|udf1|udf2|udf3|udf4|udf5|zip_code"`

2. Calculate the **hash** of the string value obtained in step 1 using **sha512** algorithm(all 
major languages would have an in-house function to calculate the hash using SHA-512).

3. Change the hash value obtained in step 2 to **UPPERCASE** and response the hash value to the 
android app.

```

```markdown
# Payment Gateway Recommendation:

You must securly store the SALT key in your server. Compromise of the same would lead to attacks.

```

> Sample Hash Generation of Payment Request for different languages has been given below:

```java
**Java Servlet Sample Code**

public class PaymentRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		// TODO Auto-generated method stub
		String salt = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; 
		
		String [] hash_columns = {"address_line_1", "address_line_2", "amount", "api_key", 
		"city", "country", "currency","description", "email", "mode", "name", "order_id", 
		"phone", "return_url", "state", "udf1", "udf2", "udf3", "udf4","udf5", "zip_code"};
		
		String hash_data = salt;
		
		for( int i = 0; i < hash_columns.length; i++)
		{
			if(request.getParameter(hash_columns[i]).length() > 0 ){
				hash_data += '|' + request.getParameter(hash_columns[i]).trim();
			}    
			
		}
		
		String hash = "";
		try {
			 hash = getHashCodeFromString(hash_data);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("hash", hash);
             	jsonResponse.addProperty("status", "Kargopolov");
       		jsonResponse.addProperty("responseCode", "Kargopolov");


		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		writer.print(jsonResponse);
        	writer.flush();

	}
	
	private static String getHashCodeFromString(String str) throws NoSuchAlgorithmException, 
	UnsupportedEncodingException {
			
		MessageDigest md = MessageDigest.getInstance("SHA-512");
	    	md.update(str.getBytes("UTF-8"));
	    	byte byteData[] = md.digest();

	    	//convert the byte to hex format method 1
	    	StringBuffer hashCodeBuffer = new StringBuffer();
	    	for (int i = 0; i < byteData.length; i++) {
	            hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
		    .substring(1));
	    	}
		return hashCodeBuffer.toString().toUpperCase();
	}
	
}

```

```php
**PHP Sample Code**

try {
	$salt="XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

	$params["api_key"]=trim($_POST["api_key"]);
	$params["amount"]=trim($_POST["amount"]);
	$params["email"]=trim($_POST["email"]);
	$params["name"]=trim($_POST["name"]);
	$params["phone"]=trim($_POST["phone"]);
	$params["order_id"]=trim($_POST["order_id"]);
	$params["currency"]=trim($_POST["currency"]);
	$params["description"]=trim($_POST["description"]);
	$params["city"]=trim($_POST["city"]);
	$params["state"]=trim($_POST["state"]);
	$params["address_line_1"]=trim($_POST["address_line_1"]);
	$params["address_line_2"]=trim($_POST["address_line_2"]);
	$params["zip_code"]=trim($_POST["zip_code"]);
	$params["country"]=trim($_POST["country"]);
	$params["return_url"]=trim($_POST["return_url"];)
	$params["mode"]=trim($_POST["mode"]);
	if(!empty($_POST["udf1"])) $params["udf1"]=trim($_POST["udf1"]);
	if(!empty($_POST["udf2"])) $params["udf2"]=trim($_POST["udf2"]);
	if(!empty($_POST["udf3"])) $params["udf3"]=trim($_POST["udf3"]);
	if(!empty($_POST["udf4"])) $params["udf4"]=trim($_POST["udf4"]);
	if(!empty($_POST["udf5"])) $params["udf5"]=trim($_POST["udf5"]);

	$hash_columns = [
		'name',
		'phone',
		'email',
		'description',
		'amount',
		'api_key',
		'order_id',
		'currency',
		'city',
		'state',
		'address_line_1',
		'address_line_2',
		'country',
		'zip_code',
		'return_url',
		'hash',
		'mode',
		'udf1',
		'udf2',
		'udf3',
		'udf4',
		'udf5'
	];

	sort($hash_columns);
	$hash_data = $salt;

	foreach ($hash_columns as $column) {
		if (isset($params[$column])) {
			if (strlen($params[$column]) > 0) {
				$hash_data .= '|' . $params[$column];
			}
		}
	}

	$hash = null;
	if (strlen($hash_data) > 0) {
		$hash = strtoupper(hash("sha512", $hash_data));
	}

	$output['hash'] = $hash;
	$output['status']=0;
	$output['responseCode']="Hash Created Successfully";

}catch(Exception $e) {
	$output['hash'] = "INVALID";
	$output['status']=1;
	$output['responseCode']=$e->getMessage();
}

echo json_encode($output);

```

```csharp
**ASP.NET Sample Code**

public partial class PaymentRequest : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            string jsonResponse = "";
            try
            {
                string hash_string = string.Empty;
                string hashValue = string.Empty;
                string SALT = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

                hash_string = SALT;
                hash_string += '|' + Request.Form["address_line_1"].Trim();
                hash_string += '|' + Request.Form["address_line_2"].Trim();
                hash_string += '|' + Request.Form["amount"].Trim();
                hash_string += '|' + Request.Form["api_key"].Trim();
                hash_string += '|' + Request.Form["city"].Trim();
                hash_string += '|' + Request.Form["country"].Trim();
                hash_string += '|' + Request.Form["currency"].Trim();
                hash_string += '|' + Request.Form["description"].Trim();
                hash_string += '|' + Request.Form["email"].Trim();
                hash_string += '|' + Request.Form["mode"].Trim();
                hash_string += '|' + Request.Form["name"].Trim();
                hash_string += '|' + Request.Form["order_id"].Trim();
                hash_string += '|' + Request.Form["phone"].Trim();
                hash_string += '|' + Request.Form["return_url"].Trim();
                hash_string += '|' + Request.Form["state"].Trim();
                if (!string.IsNullOrEmpty(Request.Form["udf1"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf1"].Trim();
                }
                if (!string.IsNullOrEmpty(Request.Form["udf2"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf2"].Trim();
                }
                if (!string.IsNullOrEmpty(Request.Form["udf3"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf3"].Trim();
                }
                if (!string.IsNullOrEmpty(Request.Form["udf4"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf4"].Trim();
                }
                if (!string.IsNullOrEmpty(Request.Form["udf5"].Trim()))
                {
                    hash_string += '|' + Request.Form["udf5"].Trim();
                }
                hash_string += '|' + Request.Form["zip_code"].Trim();

                hash_string = hash_string.Substring(0, hash_string.Length);
                hashValue = Generatehash512(hash_string).ToUpper();       

			  
	        var columns = new Dictionary<string, string>
                {
                    { "hash", hashValue},
                    { "status", 0},
                    { "responseCode", "Hash Created Successfully"},
                };                
                var jsSerializer = new JavaScriptSerializer();             
                var jsonString = jsSerializer.Serialize(columns);
			    
	       return jsonString;

            }catch (Exception ex){
                
	        var columns = new Dictionary<string, string>
               {
                   { "hash", "INVALID"},
                   { "status", 1},
                   { "responseCode", ex.Message},
               };            
               var jsSerializer = new JavaScriptSerializer();               
               var jsonString = jsSerializer.Serialize(columns);
			   
	       return jsonString;
           }
        }
        
        public string Generatehash512(string text)
        {

            byte[] message = Encoding.UTF8.GetBytes(text);

            UnicodeEncoding UE = new UnicodeEncoding();
            byte[] hashValue;
            SHA512Managed hashString = new SHA512Managed();
            string hex = "";
            hashValue = hashString.ComputeHash(message);
            foreach (byte x in hashValue)
            {
                hex += String.Format("{0:x2}", x);
            }
            return hex;

        }
}
```
-------------

> Request to your server's Payment Request API would look like below:

```javascript
{
    "amount": "2.00",
    "email": "test@gmail.com",
    "name": "Test Name",
    "phone": "9876543210",
    "order_id": "12",
    "currency": "INR",
    "description": "test",
    "city": "city",
    "state": "state",
    "zip_code": "123456",
    "country": "IND",
    "return_url": "https://yourserver.com/response_page.php",
    "mode": "TEST",
    "udf1": "udf1",
    "udf2": "udf2",
    "udf3": "udf3",
    "udf4": "udf4",
    "udf5": "udf5",
    "address_line_1": "addl1",
    "address_line_2": "addl2",
    "api_key": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXXXX"
}

```
-------------

> b. Your server should be ready to receive the payment parameters. This means you must have a API in your server that receives the response from Payment Gateway on payment completion. 

```markdown

# Response code have the below 2 components:

1. Your response must have the code to extract the hash from the Payment Gateway payment response 
and verify the hash to ensure no data tampering existed between Payment Gateway server and your 
server. You must again use SHA-512 algorithm to verify the hash.

2. If you are using the webview code given in the following section, then you must response 
the response fields that you need in a json format. 

```

```markdown

# Payment Gateway Recommendations:

At the very least, you should reverify the amount and order id field on your payment response 
API with the actual values of the amount and order id during payment initiation in your android app.
 
```

> Response Parameters List send by Payment Gateway server to your return url:

| `PARAMETER NAME` | `DESCRIPTION`                  |
| -----------------|:------------------------------:|
| `transaction_id`	   | `A unique ID that can be used to trace the transaction uniquely within Payment Gateway. Transaction IDs are alphanumeric. An example transaction ID is HDVISC1299876438` |
| `payment_mode`	   | `This tells the payment mode used by customer - example: "credit card", "debitcard", "netbanking", etc.` |
| `payment_channel`	   | `This tells the payment channel used by customer - example: "Visa", "HDFC Bank", "Paytm", etc.` |
| `payment_datetime`   | `Date and Time of this payment in "YYYY-MM-DD HH:MM:SS" format.` |
| `response_code`	   | `Status of the transaction (return code). All status codes given below` |
| `response_message`	   | `The response message associated with the transaction.` |
| `error_desc`	   | `The detailed error description, if any.` |
| `order_id`	   | `The same order_id that was originally posted by the merchant in the request.` |
| `amount`	   | `The same original amount that was sent by the merchant in the transactionrequest.` |
| `currency`	   | `This is the 3digit currency code (INR), it will be same value that was originally sent by merchant.` |
| `description`	   | `The same description that was originally sent by the merchant in the transaction request.` |
| `name`	   | `The same value that was originally sent by merchant.` |
| `email`	   | `The same value that was originally sent by merchant.` |
| `phone`	   | `The same value that was originally sent by merchant.` |
| `address_line_1`	   | `The same value that was originally sent by merchant.` |
| `address_line_2`	   | `The same value that was originally sent by merchant.` |
| `city`	   | `The same value that was originally sent by merchant.` |
| `state`	   | `The same value that was originally sent by merchant.` |
| `country`	   | `The same value that was originally sent by merchant.` |
| `zip_code`	   | `The same value that was originally sent by merchant.` |
| `udf1`	   | `The same value that was originally sent by merchant.` |
| `udf2`	   | `The same value that was originally sent by merchant.` |
| `udf3`	   | `The same value that was originally sent by merchant.` |
| `udf4`	   | `The same value that was originally sent by merchant.` |
| `udf5`	   | `The same value that was originally sent by merchant.` |
| `cardmasked`	   | `Masked card number which was used to make the transaction. For example, 437748******0069 Note: This parameter will be returned as part of the response only if the merchant’s account has been enabled for the same. Please speak to your Payment Gateway relationship manager if you would like this information to be returned to you.` |
| `hash`	   | `The hash code calculated from Trankpay server that you must use it to verify for ensuring data integrity between Payment Gateway server and your server.` |


> Sample response API code for ASP.NET and PHP is given below for reference:

```csharp
**ASP.NET Sample Response API Code**

namespace ResponseHandler_ASP_NET
{
    [ValidationProperty("false")]
    public partial class ResponseHandling : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            try
            {

                string hash = string.Empty;
                string[] keys = Request.Form.AllKeys;
                Array.Sort(keys);
                string hash_string = string.Empty;
                hash_string = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
                foreach (string hash_var in keys)
                {
                    if (Request.Form[hash_var] != "" && hash_var != "hash")
                    {
                        hash_string = hash_string + '|';
                        hash_string = hash_string + Request.Form[hash_var];
                    }
                }
                hash = Generatehash512(hash_string).ToUpper();       
                string b = Request.Form["hash"];
                if (Request.Form["hash"] == hash)
                {
                    if (Request.Form["response_code"] == "0")
                    {
                        Dictionary<string, string> data = new Dictionary<string, 
			                                               string>();

				        foreach(string item  in keys)
				        {
				        	data[item] = Request.Form[item];
				        }
				        string json = Newtonsoft.Json.JsonConvert.
					          SerializeObject(data, 				
					          Newtonsoft.Json.Formatting.Indented);
				        Response.Write(json);
                    }
                    else if(Request.Form["response_message"] == "Transaction Failed"){

                        Response.Write("Transaction is unsuccessful");
                    }
                    else
                    {
                        string response_message = Request.Form["response_message"];
                        int startIndex=response_message.IndexOf(" - ")+2;
                        int length = response_message.Length - startIndex;
                        response_message = response_message.Substring(startIndex, 
			 						    length);
                        Response.Write("Correct the below error <br />");
                        Response.Write(response_message);
                    }
                }

                else
                {
                    Response.Write("Hash value did not matched");
                }
            }

            catch (Exception ex)
            {
                Response.Write("<span style='color:red'>" + ex.Message + "</span>");

            }
        }
       
        public string Generatehash512(string text)
        {

            byte[] message = Encoding.UTF8.GetBytes(text);

            UnicodeEncoding UE = new UnicodeEncoding();
            byte[] hashValue;
            SHA512Managed hashString = new SHA512Managed();
            string hex = "";
            hashValue = hashString.ComputeHash(message);
            foreach (byte x in hashValue)
            {
                hex += String.Format("{0:x2}", x);
            }
            return hex;

        }
    }
    
```


```php
**PHP Sample Response API Code**

if(!empty($_POST)){

		if(validResponse($_POST)){
			$response = $_POST;
			$salt = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; 
			if(isset($salt) && !empty($salt)){
				$response['calculated_hash']=hashCalculate($salt, $response);
				$response['valid_hash'] = ($response['hash']==$response[
				        'calculated_hash'])?'Yes':'No';
			} else {
				$response['valid_hash']='No Hash Found';
			}

			header('Content-Type: application/json');
			if($response['valid_hash']=='Yes'){
				echo json_encode([
					'order_id'=>$_POST['order_id'],
					'amount'=>$_POST['amount'],
					'transaction_id'=>$_POST['transaction_id'],
					'response_message'=>$_POST['response_message'],
					'response_code'=>$_POST['response_code'],
				]);
			}else{
				echo json_encode(['error'=>'Hash Mismatch']);
			}
		}else {
			echo json_encode(['error'=>'Missing Mandatory Keys in Response']);
		}
}else{
	echo json_encode(['error'=>'Invalid Response']);
}

function validResponse($response){
	$mandatory_keys = [
		'order_id',
		'amount',
		'currency',
		'description',
		'name',
		'email',
		'phone',
		'city',
		'country',
		'zip_code',
		'return_url',
		'hash',
		'response_message',
		'response_code',
		'transaction_id',
	];

	$verified_values=array();

	foreach ($mandatory_keys as $key){
		array_push($verified_values,array_key_exists($key,$response)? "true":"false");
	}

	return !in_array("false",$verified_values, true);

}


function hashCalculate($salt,$input){
	unset($input['hash']);
	ksort($input);

	$hash_data = $salt;

	foreach ($input as $key=>$value) {
		if (strlen($value) > 0) {
			$hash_data .= '|' . $value;
		}
	}

	$hash = null;
	if (strlen($hash_data) > 0) {
		$hash = strtoupper(hash("sha512", $hash_data));
	}

	return $hash;
}

```

> Response Codes with Descriptions:

| `Code`  | `Response Message` |  `Description` |
|:-----:|:-----------------------------------------------|:-------------|
| `0` | `SUCCESS` | `Transaction successful` |
| `1000` | `FAILED` | `Transaction failed` |
| `1001` | `INVALID-API-KEY` | `The api key field is incorrect` |
| `1002` | `INVALID-LIVE-MODE-ACCESS` | `The live mode access is not allowed` |
| `1003` | `INVALID-ORDER-ID-FIELD` | `The order id field should to be unique` |
| `1004` | `ORDER-ID-FIELD-NOT-FOUND` | `The order id field is not found` |
| `1005` | `INVALID-AUTHENTICATION` | `Invalid authentication at bank` |
| `1006` | `WAITING-BANK-RESPONSE` | `Waiting for the response from bank` |
| `1007` | `INVALID-INPUT-REQUEST` | `Invalid input in the request message` |
| `1008` | `TRANSACTION-TAMPERED` | `Transaction tampered` |
| `1009` | `DECLINED-BY-BANK` | `Bank Declined Transaction` |
| `1010` | `INVALID-AMOUNT` | `Amount cannot be less than 1` |
| `1011` | `AUTHORIZATION-REFUSED` | `Authorization refused` |
| `1012` | `INVALID-CARD` | `Invalid Card/Member Name data` |
| `1013` | `INVALID-EXPIRY-DATE` | `Invalid expiry date` |
| `1014` | `DENIED-BY-RISK` | `Transaction denied by risk` |
| `1015` | `INSUFFICIENT-FUND` | `Insufficient Fund` |
| `1016` | `INVALID-AMOUNT-LIMIT` | `Total Amount limit set for the terminal for transactions has been crossed` |
| `1017` | `INVALID-TRANSACTION-LIMIT` | `Total transaction limit set for the terminal has been crossed` |
| `1018` | `INVALID-DEBIT-AMOUNT-LIMIT` | `Maximum debit amount limit set for the terminal for a day has been crossed` |
| `1019` | `INVALID-CREDIT-AMOUNT-LIMIT` | `Maximum credit amount limit set for the terminal for a day has been crossed` |
| `1020` | `MAXIMUM-DEBIT-AMOUNT-CROSS` | `Maximum debit amount set for per card for rolling 24 hrs has been crossed` |
| `1021` | `MAXIMUM-CREDIT-AMOUNT-CROSS` | `Maximum credit amount set for per card for rolling 24 hrs has been crossed` |
| `1022` | `MAXIMUM-TRANSACTION-CROSS` | `Maximum transaction set for per card for rolling 24 hrs has been crossed` |
| `1023` | `HASH-MISMATCH` | `Hash Mismatch` |
| `1024` | `INVALID-PARAMS` | `Invalid parameters` |
| `1025` | `INVALID-BANK-CODE` | `Invalid bank code` |
| `1026` | `INVALID-MERCHANT` | `Merchant is not active` |
| `1027` | `INVALID-TRANSACTION` | `Invalid transaction` |
| `1028` | `TRANSACTION-NOT-FOUND` | `Transaction not found` |
| `1029` | `TRANSACTION-TERMINATED` | `Transaction terminated` |
| `1030` | `TRANSACTION-INCOMPLETE` | `Transaction incomplete` |
| `1031` | `AUTO-REFUNDED` | `Transaction auto refunded` |
| `1032` | `REFUNDED` | `Transaction refunded` |
| `1033` | `SINGLE-TRANSACTION-LOWER-LIMIT-CROSS` | `The amount provided is less than transaction lower limit` |
| `1034` | `SINGLE-TRANSACTION-UPPER-LIMIT-CROSS` | `The amount provided is more than transaction upper limit` |
| `1035` | `TRANSACTION-DAILY-LIMIT-CROSS` | `The daily transaction limit is exceeded for the merchant` |
| `1036` | `TRANSACTION-MONTHLY-LIMIT-CROSS` | `The monthly transaction limit is exceeded for the merchant` |
| `1037` | `DAILY-TRANSACTION-NUMBER-CROSS` | `The daily transaction number is exceeded for the merchant` |
| `1038` | `MONTHLY-TRANSACTION-NUMBER-CROSS` | `The monthly transaction number is exceeded for the merchant` |
| `1039` | `INVALID-REFUND-AMOUNT` | `The refund amount is greater than transaction amount` |
| `1040` | `INVALID-CVV` | `Invalid Card Verification Code` |
| `1041` | `AUTO-REFUNDED-TNP` | `Transaction is auto refunded by TnP` |
| `1042` | `FAILED-NO-RESPONSE` | `Transaction failed as there was no response from bank` |
| `1043` | `TRANSACTION-CANCELLED` | `Transaction cancelled` |
| `1044` | `UNAUTHORIZED` | `Unauthorized` |
| `1045` | `FORBIDDEN` | `Forbidden Access` |
| `1046` | `TRANSACTION-ALREADY-CAPTURED` | `Transaction already captured` |
| `1047` | `AUTHORIZED` | `Transaction authorized` |
| `1048` | `CAPTURED` | `Transaction captured` |
| `1049` | `VOIDED` | `Transaction voided` |
| `1050` | `NO-RECORD-FOUND` | `No data record found for the given input` |
| `1051` | `ACQUIRER-ERROR` | `Error occurred at the bank end` |
| `1052` | `INVALID-EMAIL` | `Invalid Email ID` |
| `1053` | `INVALID-PHONE` | `Invalid phone number` |
| `9999` | `UNKNOWN-ERROR` | `Unknown error occurred` |
| `997` | `-` | `These are unhandled errors coming from banks directly.` |

-------------
    
# Webview Sample Code

> Once the payment is initiated, collect the payment fields, calculate the hash from your server and form the url post parameters. Sample code given below: 

```java

    StringBuffer requestParams=new StringBuffer("api_key="+URLDecoder.decode(SampleAppConstants.PG_API_KEY, "UTF-8"));
    requestParams.append("&amount="+URLDecoder.decode("2.00", "UTF-8"));
    requestParams.append("&email="+URLDecoder.decode("test@gmail.com", "UTF-8"));
    requestParams.append("&name="+URLDecoder.decode("Test Name", "UTF-8"));
    requestParams.append("&phone="+URLDecoder.decode("9876543210", "UTF-8"));
    requestParams.append("&order_id="+URLDecoder.decode("12", "UTF-8"));
    requestParams.append("&currency="+URLDecoder.decode(SampleAppConstants.PG_CURRENCY, "UTF-8"));
    requestParams.append("&description="+URLDecoder.decode("test", "UTF-8"));
    requestParams.append("&city="+URLDecoder.decode("city", "UTF-8"));
    requestParams.append("&state="+URLDecoder.decode("state", "UTF-8"));
    requestParams.append("&address_line_1="+URLDecoder.decode("addl1", "UTF-8"));
    requestParams.append("&address_line_2="+URLDecoder.decode("addl2", "UTF-8"));
    requestParams.append("&zip_code="+URLDecoder.decode("123456", "UTF-8"));
    requestParams.append("&country="+URLDecoder.decode(SampleAppConstants.PG_COUNTRY, "UTF-8"));
    requestParams.append("&return_url="+URLDecoder.decode(SampleAppConstants.PG_RETURN_URL, "UTF-8"));
    requestParams.append("&mode="+URLDecoder.decode(SampleAppConstants.PG_MODE, "UTF-8"));
    requestParams.append("&udf1="+URLDecoder.decode("udf1", "UTF-8"));
    requestParams.append("&udf2="+URLDecoder.decode("udf2", "UTF-8"));
    requestParams.append("&udf3="+URLDecoder.decode("udf3", "UTF-8"));
    requestParams.append("&udf4="+URLDecoder.decode("udf4", "UTF-8"));
    requestParams.append("&udf5="+URLDecoder.decode("udf5", "UTF-8"));
    requestParams.append("&hash="+URLDecoder.decode("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", "UTF-8"));  
           
```

> Request parameters List(mandatory and non mandatory fields) that needs to be posted to Payment Gateway Server during payment initiation:


| `PARAMETER NAME` | `DESCRIPTION`                  | `REQUIRED`           | `DATATYPE`  |
| -----------------|:------------------------------:|:--------------------:|:-----------:|
| `api_key`        | `Payment Gateway would assign a unique 40-digit merchant key to you. This key is exclusive to your business/login account.If you have multiple login accounts, there will necessarily be one different api_key per login account that is assigned to you.` | `Mandatory` | `String - Max:40.` |
| `order_id`	   | `This is your (merchant) reference number. It must be unique for every transaction. We do perform a validation at our end and do not allow duplicate order_ids for the same merchant.` | `Mandatory` | `String - Max:30.` |
| `mode`	   | `This is the payment mode ("TEST" or "LIVE" are valid values). "LIVE" is the default value when not specified.` | `Optional` | `String - Max:4.` |
| `amount`	   | `This is the payment amount.` | `Mandatory` | `Decimal - Max Digits Before Decimal:15, Max Digits after Decimal:2.` |
| `currency`	   | `This is the 3-digit currency code (INR).` | `Mandatory` | `String - Max:3.` |
| `description`	   | `Brief description of product or service that the customer is being charged for.` | `Mandatory` | `String - Max:200.` |
| `name`	   | `Name of customer.` | `Mandatory` | `String - Max:200.` |
| `email`	   | `Customer email address.` | `Mandatory` | `String - Max:200.` |
| `phone`	   | `Customer phone number.` | `Mandatory` | `String - Max:30.` |
| `address_line_1`	   | `Customer's address line 1.` | `Optional` | `String - Max:255.` |
| `address_line_2`	   | `Customer's address line 2.` | `Optional` | `String - Max:255.` |
| `city`	   | `Customer City.` | `Mandatory` | `String - Max:50.` |
| `state`	   | `Customer State.` | `Optional` | `String - Max:50.` |
| `country`	   | `Customer Country.` | `Mandatory` | `String - Max:50.` |
| `zip_code`	   | `Customer Zipcode.` | `Mandatory` | `String - Max:10.` |
| `timeout_duration`	   | `Timeout duration (in seconds).` | `Optional` | `Integer - Min:0,Max:1000.` |
| `udf1`	   | `User defined field 1.` | `Optional` | `String - Max:300.` |
| `udf2`	   | `User defined field 2.` | `Optional` | `String - Max:300.` |
| `udf3`	   | `User defined field 3.` | `Optional` | `String - Max:300.` |
| `udf4`	   | `User defined field 4.` | `Optional` | `String - Max:300.` |
| `udf5`	   | `User defined field 5.` | `Optional` | `String - Max:300.` |
| `return_url`	   | `Your return URL where Payment Gateway will send all the payment response parameters after a transaction.` | `Mandatory` | `String - Max:200.` |
| `return_url_failure`	   | `Payment Gateway will send all failed transaction response parameters to this URL if specified, else, it will send the failed response to the "return_url" parameter` | `Optional` | `String - Max:200.` |
| `return_url_cancel`	   | `Payment Gateway will send all cancelled transaction response parameters to this URL if specified, else, it will send the cancelled response to the "return_url" parameter.` | `Optional` | `String - Max:200.` |
| `percent_tdr_by_user`	   | `Percent of tdr amount paid by user.` | `Optional` | `Integer - Min:0,Max:100.` |
| `flatfee_tdr_by_user`	   | `Fixed fee paid by user.` | `Optional` | `Integer - Min:0,Max:99999999.` |
| `show_convenience_fee`	   | `Controls whether the convenience fee amount (for surcharge merchants) is displayed to the customer (on the payment page) or not.` | `Optional` | `String - Max:1.` |
| `split_enforce_strict`	   | `Controls whether payment is required to be split before settlement. By default it is set to ‘n’, If this is set to ‘y’ then settlement will be on HOLD until splitsettlement api is called to provide split information.` | `Optional` | `String - Max:1.` |
| `payment_options`	   | `payment options to be displayed such credit card(cc), netbanking(nb), wallet(w) and atm card(atm).Tabs will be displayed by order in which values are sent. Values accepted are: cc,nb,w,atm (comma separated string).` | `Optional` | `String - Max:20.` |
| `payment_page_display_text`	   | `This text will be displayed below the logo on payment page.` | `Optional` | `String - Max:200.` |
| `hash`	   | `Checksum to ensure data integrity during server to server calls.` | `Mandatory` | `String - Max:200.` |

> Post the parameters to the Payment Gateway Payment URL and intercept your response API with a javascript to receive the response parameters.
Finally parse the json response string to get the values of individual fields.

```java
webview.setWebViewClient(new WebViewClient() {
    @Override
    public void onPageFinished(WebView view, String url) {
        pb.setVisibility(View.GONE);

        if(url.equals(SampleAppConstants.PG_RETURN_URL)){
            view.setVisibility(View.GONE);
            view.loadUrl("javascript:HtmlViewer.showHTML" +
                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
        }

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap facIcon) {
        pb.setVisibility(View.VISIBLE);
    }

});

WebSettings webSettings = webview.getSettings();
webSettings.setJavaScriptEnabled(true);
webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
webSettings.setDomStorageEnabled(true);
webview.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
webview.postUrl(SampleAppConstants.PG_HOSTNAME+"/v1/paymentrequest",requestParams.toString().getBytes());

class MyJavaScriptInterface {
    @JavascriptInterface
    public void showHTML(String html) {		
		try{
                JSONObject response = new JSONObject(Html.fromHtml(html).toString());
                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    System.out.println(response.getString(key));
                }

       }catch (JSONException e){
           e.printStackTrace();
       }
    }
}

```
