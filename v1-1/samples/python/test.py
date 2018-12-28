from hyperdocs import requestFn

#requestFn("./sample.jpg", "image", "readKYC")
#requestFn("./sample.pdf", "pdf", "readKYC")
# valid file types : image, pdf
# valid endPoints : readKYC, readPAN, readPassport, readAadhaar
# Though readKYC works on all documents. Use the appropriate endPoint if the document
# type is known as this would provide a marginally higher accuracy and better
# performance.
print(requestFn("./sample.jpg", "image", "readKYC"))
