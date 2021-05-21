def update_interface(implementation, interface):
    with open(implementation, "r") as f:
        lines = f.readlines()

    output = "public interface ServerI extends Remote {\n"
    for line in lines:
        #ignore javadoc comments and constructors
        if "throws RemoteException" in line and \
            "* @throws RemoteException" not in line and\
            "public void rebind(Registry r, String service) throws RemoteException" not in line and \
            "Server" not in line:
            output += line.replace("{", ";")

    output += "\n}"

    with open(interface, "r") as f:
        code = f.readlines()

    header = ""

    for line in code:
        if "public interface ServerI extends Remote {" in line:
            break
        header += line

    with open(interface, "w") as f:
        f.write(header + output)

def main():
    update_interface("rmi/Server.java", "common/ServerI.java")

if __name__ == "__main__":
    main()


